package com.momentum.app.ui.screens.places

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.*
import com.momentum.app.ui.animations.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesScreen(navController: NavController, viewModel: PlacesViewModel = viewModel()) {
    val context = LocalContext.current
    val recentPlaces by viewModel.recentPlaces.collectAsState()

    var hasFineLocation by remember { mutableStateOf(false) }
    var hasCoarseLocation by remember { mutableStateOf(false) }
    var lastLat by remember { mutableStateOf<Double?>(null) }
    var lastLng by remember { mutableStateOf<Double?>(null) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasFineLocation = results[Manifest.permission.ACCESS_FINE_LOCATION] == true
        hasCoarseLocation = results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun fetchLocation() {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    lastLat = loc.latitude
                    lastLng = loc.longitude
                } else {
                    val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10_000)
                        .setMinUpdateIntervalMillis(5_000)
                        .setMaxUpdates(1)
                        .build()
                    fusedClient.requestLocationUpdates(
                        request,
                        object : LocationCallback() {
                            override fun onLocationResult(result: LocationResult) {
                                fusedClient.removeLocationUpdates(this)
                                result.lastLocation?.let {
                                    lastLat = it.latitude
                                    lastLng = it.longitude
                                }
                            }
                        },
                        Looper.getMainLooper()
                    )
                }
            }
        } catch (_: SecurityException) { /* permissions not granted */ }
    }

    LaunchedEffect(hasFineLocation, hasCoarseLocation) {
        if (hasFineLocation || hasCoarseLocation) fetchLocation()
    }

    fun openMapsQuery(query: String) {
        val uri = if (lastLat != null && lastLng != null) {
            Uri.parse("geo:${lastLat},${lastLng}?q=${Uri.encode(query)}")
        } else {
            Uri.parse("geo:0,0?q=${Uri.encode(query)}")
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        ContextCompat.startActivity(context, intent, null)
    }

    fun recordRecentPlace(name: String, category: String) {
        viewModel.addRecentPlace(name, category)
        openMapsQuery(name)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lugares cercanos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Explora parques, naturaleza y apoyo en salud mental",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .animatedFadeIn()
                    .animatedScale()
            )

            // Recent places section
            if (recentPlaces.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().animatedSlideUp(40)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Búsquedas recientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            TextButton(onClick = { viewModel.clearAllRecent() }) {
                                Text("Limpiar", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 200.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(recentPlaces) { place ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = place.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(place.searchedAt)),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { openMapsQuery(place.name) }) {
                                        Icon(Icons.Default.Place, contentDescription = "Buscar de nuevo")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().animatedSlideUp(80)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Naturaleza y recreación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    PlaceButton(
                        text = "Buscar parques",
                        icon = Icons.Default.Park,
                        onClick = { recordRecentPlace("parques cerca de mi", "park") },
                        color = Color(0xFF4CAF50)
                    )
                    PlaceButton(
                        text = "Senderos y naturaleza",
                        icon = Icons.Default.Terrain,
                        onClick = { recordRecentPlace("senderos naturales", "trail") }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().animatedSlideUp(140)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Apoyo en salud mental", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    PlaceButton(
                        text = "Buscar psicólogos",
                        icon = Icons.Default.Healing,
                        onClick = { recordRecentPlace("psicologo salud mental", "psychologist") },
                        color = MaterialTheme.colorScheme.secondary
                    )
                    PlaceButton(
                        text = "Centros de apoyo",
                        icon = Icons.Default.Healing,
                        onClick = { recordRecentPlace("centro de salud mental", "support_center") }
                    )
                }
            }

            if (!(hasFineLocation || hasCoarseLocation)) {
                AssistChip(
                    onClick = {
                        permissionsLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    label = { Text("Conceder permiso de ubicación para mejores resultados") },
                    leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null) }
                )
            } else {
                Text(
                    text = if (lastLat != null) "Ubicación aproximada: ${"%.4f".format(lastLat)}, ${"%.4f".format(lastLng)}" else "Obteniendo ubicación...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PlaceButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().bounceClick(),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text)
    }
}
