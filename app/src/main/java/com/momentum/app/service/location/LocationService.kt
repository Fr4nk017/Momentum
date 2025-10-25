package com.momentum.app.service.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.CancellationSignal
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Service class to handle location-related operations
 * @param context Application context
 */
@Singleton
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    /**
     * Gets the last known location from the device
     * @return Result containing the last known location or null if not available
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Result<Location?> = try {
        suspendCancellableCoroutine { continuation ->
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        continuation.resume(Result.success(location))
                    }
                    .addOnFailureListener { exception ->
                        continuation.resume(Result.failure(exception))
                    }
            } catch (e: SecurityException) {
                continuation.resume(Result.failure(e))
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Gets the current location with high accuracy
     * @param timeoutMs Timeout in milliseconds for the location request
     * @return Result containing the current location or null if not available
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(timeoutMs: Long = 5000): Result<Location?> = try {
        val location = withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine { continuation ->
                var cancellationSignal: CancellationSignal? = null
                
                try {
                    cancellationSignal = CancellationSignal()
                    
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationSignal
                    ).addOnSuccessListener { location ->
                        continuation.resume(Result.success(location))
                    }.addOnFailureListener { exception ->
                        continuation.resume(Result.failure(exception))
                    }

                    // Handle coroutine cancellation
                    continuation.invokeOnCancellation {
                        cancellationSignal?.cancel()
                    }
                } catch (e: SecurityException) {
                    continuation.resume(Result.failure(e))
                }
            }
        }

        location ?: Result.failure(TimeoutException("Location request timed out after ${timeoutMs}ms"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    companion object {
        const val DEFAULT_TIMEOUT_MS = 5000L
    }
}

/**
 * Wrapper class for location errors
 */
sealed class LocationError : Exception() {
    object PermissionDenied : LocationError()
    object LocationDisabled : LocationError()
    data class Timeout(val timeoutMs: Long) : LocationError()
    data class Unknown(override val cause: Throwable?) : LocationError()
}

/**
 * Extension to get user-friendly error messages
 */
fun LocationError.toUserMessage(): String = when (this) {
    is LocationError.PermissionDenied -> "Permisos de ubicación denegados"
    is LocationError.LocationDisabled -> "La ubicación está desactivada"
    is LocationError.Timeout -> "No se pudo obtener la ubicación (timeout: ${timeoutMs}ms)"
    is LocationError.Unknown -> "Error desconocido: ${cause?.message}"
}