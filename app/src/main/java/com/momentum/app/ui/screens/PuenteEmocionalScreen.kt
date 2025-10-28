package com.momentum.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.momentum.app.navigation.Routes
import androidx.compose.ui.res.stringResource
import com.momentum.app.R
import com.momentum.app.ui.animations.*
import com.momentum.app.viewmodel.HomeViewModel
import com.momentum.app.data.suggestions.ActionType
import androidx.compose.material3.ModalBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuenteEmocionalScreen(
    navController: NavController,
    viewModel: BienestarViewModel,
    homeViewModel: HomeViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val homeState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(estado.perfil.correo) {
        if (estado.perfil.correo.isNotEmpty()) homeViewModel.initialize(estado.perfil.correo)
    }

    var showIntensitySheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier
                .animatedFadeIn(delay = 0)
                .animatedScale(delay = 0)
            ) {
                Text(
                    text = "¡Hola, ${estado.perfil.nombre}! 🔥 Día ${homeState.dayStreak}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tu equilibrio mental es prioridad",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (homeState.dayStreak >= 7) {
                    AssistChip(onClick = {}, label = { Text("Logro: Semana completa desbloqueado") })
                }
                Text(
                    text = homeState.joke,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            
            IconButton(
                onClick = { navController.navigate(Routes.Perfil.name) }
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = estado.perfil.nombre.firstOrNull()?.toString()?.uppercase() ?: "U",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Check-in emocional interactivo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animatedSlideUp(delay = 200)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Registra tu estado actual",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                
                // Grid de emociones
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(160.dp)
                ) {
                    items(homeViewModel.emotionGrid) { emoji ->
                        val idx = homeViewModel.emotionGrid.indexOf(emoji)
                        ElevatedButton(
                            onClick = {
                                homeViewModel.selectMood(emoji)
                                showIntensitySheet = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animatedFadeIn(delay = getStaggeredDelay(idx, baseDelay = 50))
                        ) {
                            Text(emoji, style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
            }
        }

        // Sugerencia de respiración
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animatedSlideUp(delay = 280)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val suggestion = homeState.suggestion ?: com.momentum.app.data.suggestions.Suggestion(
                    badge = "💡 Recomendación personalizada",
                    title = "Respiración 4-4-6 - 5 repeticiones",
                    description = "Perfecto para tu estado de ánimo actual",
                    actionLabel = "Comenzar ahora",
                    actionType = ActionType.BREATHING_446
                )
                AssistChip(onClick = {}, label = { Text(suggestion.badge) })
                Text(suggestion.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
                Text(suggestion.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            when (suggestion.actionType) {
                                ActionType.BREATHING_446 -> {
                                    viewModel.iniciarEjercicioRespiracion()
                                    navController.navigate(Routes.Chat.name)
                                }
                                ActionType.OPEN_DIARY -> navController.navigate(Routes.Diario.name)
                                ActionType.OPEN_CHAT -> navController.navigate(Routes.Chat.name)
                                ActionType.MINDFULNESS -> navController.navigate(Routes.Chat.name)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                        modifier = Modifier.bounceClick()
                    ) {
                        Text(suggestion.actionLabel, color = Color.White)
                    }
                }
            }
        }

        // Accesos rápidos en grid
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animatedSlideUp(delay = 320)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Accesos rápidos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
                LazyVerticalGrid(columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(160.dp)) {
                    items(listOf("📝" to "Diario", "💬" to "Chat", "📊" to "Progreso", "🌬" to "Ejercicios", "🎯" to "Metas", "🔔" to "Recordatorios")) { (icon, label) ->
                        ElevatedButton(onClick = {
                            when (label) {
                                "Diario" -> navController.navigate(Routes.Diario.name)
                                "Chat" -> navController.navigate(Routes.Chat.name)
                                "Progreso" -> navController.navigate(Routes.Progreso.name)
                                "Ejercicios" -> navController.navigate(Routes.Chat.name)
                                "Metas" -> navController.navigate(Routes.PuenteEmocional.name) // placeholder
                                "Recordatorios" -> navController.navigate(Routes.Perfil.name)
                            }
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text("$icon  $label")
                        }
                    }
                }
                if (!homeState.hasEntryToday) {
                    AssistChip(onClick = { navController.navigate(Routes.Diario.name) }, label = { Text("📝 Tienes 1 pendiente en Diario") })
                }
            }
        }

        // CTA Profesional de la salud
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animatedSlideUp(delay = 340),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "¿Necesitas hablar con un profesional?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { navController.navigate(Routes.Chat.name) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier.bounceClick()
                ) {
                    Text("Contactar", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation
        BottomNavigationBar(
            currentRoute = Routes.PuenteEmocional.name,
            navController = navController
        )
    }

    // BottomSheet de intensidad
    if (showIntensitySheet && homeState.selectedMood != null) {
        ModalBottomSheet(onDismissRequest = { showIntensitySheet = false }) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("${homeState.selectedMood} - ¿Qué tan intenso?", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf(1 to "Leve", 2 to "Moderado", 3 to "Intenso").forEach { (level, label) ->
                        OutlinedButton(onClick = {
                            homeViewModel.selectIntensity(level)
                            showIntensitySheet = false
                        }, modifier = Modifier.weight(1f)) { Text(label) }
                    }
                }
                Button(onClick = { homeViewModel.saveQuickCheckIn(); showIntensitySheet = false }, enabled = homeState.selectedIntensity > 0) {
                    Text("Guardar check-in")
                }
            }
        }
    }
}