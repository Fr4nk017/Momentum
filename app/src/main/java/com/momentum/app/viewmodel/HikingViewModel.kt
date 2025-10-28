package com.momentum.app.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.data.DatabaseProvider
import com.momentum.app.data.local.HikingSessionEntity
import com.momentum.app.data.local.LocationPointEntity
import com.momentum.app.data.repository.HikingRepository
import com.momentum.app.service.HikingService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HikingViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: HikingRepository
    private var hikingService: HikingService? = null
    private var isBound = false
    
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()
    
    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()
    
    private val _distance = MutableStateFlow(0f)
    val distance: StateFlow<Float> = _distance.asStateFlow()
    
    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()
    
    private val _speed = MutableStateFlow(0f)
    val speed: StateFlow<Float> = _speed.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    private val _calories = MutableStateFlow(0f)
    val calories: StateFlow<Float> = _calories.asStateFlow()
    
    private val _currentRoute = MutableStateFlow<List<LocationPointEntity>>(emptyList())
    val currentRoute: StateFlow<List<LocationPointEntity>> = _currentRoute.asStateFlow()
    
    private val _hikingHistory = MutableStateFlow<List<HikingSessionEntity>>(emptyList())
    val hikingHistory: StateFlow<List<HikingSessionEntity>> = _hikingHistory.asStateFlow()
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as HikingService.LocalBinder
            hikingService = binder.getService()
            isBound = true
            
            // Observe service stats
            viewModelScope.launch {
                hikingService?.distance?.collect { _distance.value = it }
            }
            viewModelScope.launch {
                hikingService?.steps?.collect { _steps.value = it }
            }
            viewModelScope.launch {
                hikingService?.speed?.collect { _speed.value = it }
            }
            viewModelScope.launch {
                hikingService?.duration?.collect { _duration.value = it }
            }
            viewModelScope.launch {
                hikingService?.calories?.collect { _calories.value = it }
            }
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            hikingService = null
            isBound = false
        }
    }
    
    init {
        val db = DatabaseProvider.getDatabase(application)
        repository = HikingRepository(
            db.hikingSessionDao(),
            db.locationPointDao()
        )
        
        // Load hiking history
        viewModelScope.launch {
            repository.getCompletedSessions().collect { sessions ->
                _hikingHistory.value = sessions
            }
        }
    }
    
    fun startHiking(context: Context) {
        val intent = Intent(context, HikingService::class.java).apply {
            action = HikingService.ACTION_START
        }
        context.startForegroundService(intent)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        
        _isTracking.value = true
        _isPaused.value = false
    }
    
    fun pauseHiking(context: Context) {
        val intent = Intent(context, HikingService::class.java).apply {
            action = HikingService.ACTION_PAUSE
        }
        context.startService(intent)
        _isPaused.value = true
    }
    
    fun resumeHiking(context: Context) {
        val intent = Intent(context, HikingService::class.java).apply {
            action = HikingService.ACTION_RESUME
        }
        context.startService(intent)
        _isPaused.value = false
    }
    
    fun stopHiking(context: Context) {
        val intent = Intent(context, HikingService::class.java).apply {
            action = HikingService.ACTION_STOP
        }
        context.startService(intent)
        
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
        
        _isTracking.value = false
        _isPaused.value = false
        
        // Reset stats
        _distance.value = 0f
        _steps.value = 0
        _speed.value = 0f
        _duration.value = 0L
        _calories.value = 0f
        _currentRoute.value = emptyList()
    }
    
    fun loadSessionRoute(sessionId: Long) {
        viewModelScope.launch {
            repository.observeLocationPointsForSession(sessionId).collect { points ->
                _currentRoute.value = points
            }
        }
    }
    
    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
        }
    }
    
    fun formatDuration(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        val hours = milliseconds / (1000 * 60 * 60)
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
    
    fun formatDistance(meters: Float): String {
        return if (meters >= 1000) {
            String.format("%.2f km", meters / 1000)
        } else {
            String.format("%.0f m", meters)
        }
    }
    
    fun formatSpeed(metersPerSecond: Float): String {
        val kmPerHour = metersPerSecond * 3.6f
        return String.format("%.1f km/h", kmPerHour)
    }
    
    fun formatCalories(calories: Float): String {
        return String.format("%.0f kcal", calories)
    }
    
    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            getApplication<Application>().unbindService(serviceConnection)
            isBound = false
        }
    }
}
