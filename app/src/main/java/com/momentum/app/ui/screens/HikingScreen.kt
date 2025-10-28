package com.momentum.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.momentum.app.viewmodel.HikingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikingScreen(
    viewModel: HikingViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    
    val isTracking by viewModel.isTracking.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val distance by viewModel.distance.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val speed by viewModel.speed.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val calories by viewModel.calories.collectAsState()
    val currentRoute by viewModel.currentRoute.collectAsState()
    
    var hasLocationPermission by remember { mutableStateOf(false) }
    var hasActivityPermission by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    // Check permissions
    LaunchedEffect(Unit) {
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        hasActivityPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (!hasLocationPermission || !hasActivityPermission) {
            showPermissionDialog = true
        }
    }
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }
    
    val activityPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasActivityPermission = isGranted
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Senderismo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            HikingControlsFAB(
                isTracking = isTracking,
                isPaused = isPaused,
                hasLocationPermission = hasLocationPermission,
                hasActivityPermission = hasActivityPermission,
                onStart = {
                    if (hasLocationPermission && hasActivityPermission) {
                        viewModel.startHiking(context)
                    } else {
                        showPermissionDialog = true
                    }
                },
                onPause = { viewModel.pauseHiking(context) },
                onResume = { viewModel.resumeHiking(context) },
                onStop = { viewModel.stopHiking(context) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Map placeholder (simple route visualization)
            RouteMapPlaceholder(
                route = currentRoute,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            
            // Stats section
            StatsSection(
                distance = viewModel.formatDistance(distance),
                duration = viewModel.formatDuration(duration),
                speed = viewModel.formatSpeed(speed),
                steps = steps.toString(),
                calories = viewModel.formatCalories(calories),
                isTracking = isTracking,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    // Permission dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permisos requeridos") },
            text = {
                Text(
                    "Esta función requiere permisos de ubicación y actividad física " +
                    "para rastrear tu senderismo y contar tus pasos."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (!hasLocationPermission) {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    if (!hasActivityPermission) {
                        activityPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                    }
                    showPermissionDialog = false
                }) {
                    Text("Conceder permisos")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun HikingControlsFAB(
    isTracking: Boolean,
    isPaused: Boolean,
    hasLocationPermission: Boolean,
    hasActivityPermission: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isTracking) {
            // Stop button
            FloatingActionButton(
                onClick = onStop,
                containerColor = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Stop, "Detener")
            }
            
            // Pause/Resume button
            FloatingActionButton(
                onClick = if (isPaused) onResume else onPause,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    if (isPaused) "Reanudar" else "Pausar"
                )
            }
        } else {
            // Start button
            FloatingActionButton(
                onClick = onStart,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    "Iniciar",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun RouteMapPlaceholder(
    route: List<com.momentum.app.data.local.LocationPointEntity>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (route.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Map,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Inicia tu caminata para ver la ruta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        } else {
            // Simple route display (in a real app, use Google Maps SDK)
            Text(
                "${route.size} puntos de ruta registrados",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatsSection(
    distance: String,
    duration: String,
    speed: String,
    steps: String,
    calories: String,
    isTracking: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Distance and Duration (primary stats)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    label = "Distancia",
                    value = distance,
                    icon = Icons.Default.Route,
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                StatCard(
                    label = "Duración",
                    value = duration,
                    icon = Icons.Default.Timer,
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
            }
            
            // Speed, Steps, and Calories (secondary stats)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    label = "Velocidad",
                    value = speed,
                    icon = Icons.Default.Speed,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatCard(
                    label = "Pasos",
                    value = steps,
                    icon = Icons.Default.DirectionsWalk,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatCard(
                    label = "Calorías",
                    value = calories,
                    icon = Icons.Default.LocalFireDepartment,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Status indicator
            AnimatedVisibility(
                visible = isTracking,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Rastreando actividad...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (isPrimary) 1f else 0.9f,
        animationSpec = tween(300)
    )
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPrimary) 2.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(if (isPrimary) 28.dp else 20.dp),
                tint = if (isPrimary) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = if (isPrimary) 
                    MaterialTheme.typography.headlineSmall 
                else 
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPrimary) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isPrimary) 
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
