package com.momentum.app.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.momentum.app.R
import com.momentum.app.data.DatabaseProvider
import com.momentum.app.data.local.HikingSessionEntity
import com.momentum.app.data.local.LocationPointEntity
import com.momentum.app.data.repository.HikingRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

class HikingService : Service(), SensorEventListener {
    
    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        
        const val CHANNEL_ID = "HikingServiceChannel"
        const val NOTIFICATION_ID = 1001
        
        private const val LOCATION_UPDATE_INTERVAL = 5000L // 5 seconds
        private const val LOCATION_FASTEST_INTERVAL = 2000L // 2 seconds
        private const val MIN_DISTANCE_THRESHOLD = 5f // 5 meters minimum to count
    }
    
    private val binder = LocalBinder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var repository: HikingRepository
    
    private var currentSessionId: Long? = null
    private var lastLocation: Location? = null
    private var initialStepCount: Int = 0
    private var currentStepCount: Int = 0
    private var isPaused = false
    
    // Real-time stats
    private val _distance = MutableStateFlow(0f) // meters
    private val _steps = MutableStateFlow(0)
    private val _speed = MutableStateFlow(0f) // m/s
    private val _duration = MutableStateFlow(0L) // milliseconds
    private val _calories = MutableStateFlow(0f)
    
    val distance: StateFlow<Float> = _distance.asStateFlow()
    val steps: StateFlow<Int> = _steps.asStateFlow()
    val speed: StateFlow<Float> = _speed.asStateFlow()
    val duration: StateFlow<Long> = _duration.asStateFlow()
    val calories: StateFlow<Float> = _calories.asStateFlow()
    
    private var sessionStartTime: Long = 0
    private var pausedTime: Long = 0
    private var totalPausedDuration: Long = 0
    
    inner class LocalBinder : Binder() {
        fun getService(): HikingService = this@HikingService
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    override fun onCreate() {
        super.onCreate()
        
        val db = DatabaseProvider.getDatabase(applicationContext)
        repository = HikingRepository(
            db.hikingSessionDao(),
            db.locationPointDao()
        )
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        
        createNotificationChannel()
        setupLocationCallback()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startHiking()
            ACTION_PAUSE -> pauseHiking()
            ACTION_RESUME -> resumeHiking()
            ACTION_STOP -> stopHiking()
        }
        return START_STICKY
    }
    
    @SuppressLint("MissingPermission")
    private fun startHiking() {
        if (currentSessionId != null) return
        
        sessionStartTime = System.currentTimeMillis()
        isPaused = false
        totalPausedDuration = 0
        
        // Reset stats
        _distance.value = 0f
        _steps.value = 0
        _speed.value = 0f
        _duration.value = 0L
        _calories.value = 0f
        lastLocation = null
        
        // Start step counter
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        
        // Create new session in database
        serviceScope.launch {
            val session = HikingSessionEntity(
                startTime = sessionStartTime,
                endTime = null,
                totalDistance = 0f,
                totalSteps = 0,
                totalCalories = 0f,
                averageSpeed = 0f,
                maxSpeed = 0f,
                duration = 0L,
                isCompleted = false
            )
            currentSessionId = repository.createSession(session)
        }
        
        // Start location updates
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        ).apply {
            setMinUpdateIntervalMillis(LOCATION_FASTEST_INTERVAL)
            setWaitForAccurateLocation(true)
        }.build()
        
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        
        // Start duration counter
        startDurationCounter()
        
        // Start foreground service
        startForeground(NOTIFICATION_ID, createNotification())
    }
    
    private fun pauseHiking() {
        if (isPaused) return
        isPaused = true
        pausedTime = System.currentTimeMillis()
        
        fusedLocationClient.removeLocationUpdates(locationCallback)
        updateNotification("Pausado")
    }
    
    @SuppressLint("MissingPermission")
    private fun resumeHiking() {
        if (!isPaused) return
        isPaused = false
        
        totalPausedDuration += System.currentTimeMillis() - pausedTime
        
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        ).apply {
            setMinUpdateIntervalMillis(LOCATION_FASTEST_INTERVAL)
            setWaitForAccurateLocation(true)
        }.build()
        
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        
        updateNotification("En progreso")
    }
    
    private fun stopHiking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        sensorManager.unregisterListener(this)
        
        // Save final session data
        serviceScope.launch {
            currentSessionId?.let { sessionId ->
                val session = repository.getSessionById(sessionId)
                session?.let {
                    val updatedSession = it.copy(
                        endTime = System.currentTimeMillis(),
                        totalDistance = _distance.value,
                        totalSteps = _steps.value,
                        totalCalories = _calories.value,
                        averageSpeed = if (_duration.value > 0) {
                            _distance.value / (_duration.value / 1000f)
                        } else 0f,
                        duration = _duration.value,
                        isCompleted = true
                    )
                    repository.updateSession(updatedSession)
                }
            }
            
            // Reset state
            currentSessionId = null
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }
    
    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (isPaused) return
                
                val location = locationResult.lastLocation ?: return
                
                // Calculate distance if we have a previous location
                lastLocation?.let { previous ->
                    val distance = previous.distanceTo(location)
                    
                    // Only add if movement is significant (reduces GPS drift)
                    if (distance >= MIN_DISTANCE_THRESHOLD) {
                        _distance.value += distance
                        _speed.value = location.speed
                        
                        // Calculate calories (rough estimate: 0.05 calories per meter per kg)
                        // Assuming average weight of 70kg
                        _calories.value = _distance.value * 0.05f * 70f / 1000f
                        
                        // Save location point
                        currentSessionId?.let { sessionId ->
                            serviceScope.launch {
                                repository.addLocationPoint(
                                    LocationPointEntity(
                                        sessionId = sessionId,
                                        latitude = location.latitude,
                                        longitude = location.longitude,
                                        altitude = location.altitude,
                                        speed = location.speed,
                                        accuracy = location.accuracy,
                                        timestamp = location.time
                                    )
                                )
                            }
                        }
                        
                        updateNotification()
                    }
                }
                
                lastLocation = location
            }
        }
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount == 0) {
                initialStepCount = event.values[0].toInt()
            }
            currentStepCount = event.values[0].toInt()
            _steps.value = currentStepCount - initialStepCount
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }
    
    private fun startDurationCounter() {
        serviceScope.launch {
            while (currentSessionId != null) {
                if (!isPaused) {
                    _duration.value = System.currentTimeMillis() - sessionStartTime - totalPausedDuration
                }
                delay(1000L)
            }
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Seguimiento de Senderismo",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificaciones para el seguimiento de actividades de senderismo"
            }
            
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(status: String = "En progreso"): Notification {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val distanceKm = _distance.value / 1000f
        val durationMinutes = _duration.value / 60000L
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Senderismo $status")
            .setContentText("${String.format("%.2f", distanceKm)} km • ${durationMinutes} min • ${_steps.value} pasos")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    private fun updateNotification(status: String = "En progreso") {
        val notification = createNotification(status)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        sensorManager.unregisterListener(this)
        serviceScope.cancel()
    }
}
