package com.momentum.app.util.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat

/**
 * Helper class to handle location permissions in a Compose-friendly way
 * @param context Android context
 * @param onPermissionGranted Callback when permissions are granted
 * @param onPermissionDenied Callback when permissions are denied
 * @param onShowRationale Callback to show rationale UI when needed
 */
class LocationPermissionHelper(
    private val context: Context,
    private val onPermissionGranted: () -> Unit = {},
    private val onPermissionDenied: () -> Unit = {},
    private val onShowRationale: () -> Unit = {}
) {
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /**
     * Check if location permissions are granted
     * @return true if both FINE and COARSE location permissions are granted
     */
    fun hasLocationPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Check if GPS is enabled in device settings
     * @return true if location services are enabled
     */
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    /**
     * Check if the app should show permission rationale UI
     * @return true if rationale should be shown for any location permission
     */
    fun shouldShowRationale(activity: androidx.activity.ComponentActivity): Boolean {
        return requiredPermissions.any {
            activity.shouldShowRequestPermissionRationale(it)
        }
    }

    companion object {
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    }
}

/**
 * Composable to handle location permissions using Activity Result API
 * @param onPermissionGranted Callback when permissions are granted
 * @param onPermissionDenied Callback when permissions are denied
 * @param onShowRationale Callback to show rationale UI
 * @return LocationPermissionHelper instance
 */
@Composable
fun rememberLocationPermissionHelper(
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {},
    onShowRationale: () -> Unit = {}
): LocationPermissionState {
    val context = LocalContext.current

    val permissionHelper = remember(context) {
        LocationPermissionHelper(
            context = context,
            onPermissionGranted = onPermissionGranted,
            onPermissionDenied = onPermissionDenied,
            onShowRationale = onShowRationale
        )
    }

    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    return remember(permissionHelper, multiplePermissionLauncher) {
        LocationPermissionState(
            hasPermissions = permissionHelper.hasLocationPermissions(),
            isLocationEnabled = permissionHelper.isLocationEnabled(),
            requestPermissions = {
                multiplePermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        )
    }
}

/**
 * State holder for location permissions
 * @property hasPermissions Whether location permissions are granted
 * @property isLocationEnabled Whether location services are enabled
 * @property requestPermissions Function to request location permissions
 */
data class LocationPermissionState(
    val hasPermissions: Boolean,
    val isLocationEnabled: Boolean,
    val requestPermissions: () -> Unit
)