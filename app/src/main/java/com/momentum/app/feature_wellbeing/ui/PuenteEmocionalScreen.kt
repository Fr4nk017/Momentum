package com.momentum.app.feature_wellbeing.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.momentum.app.feature_wellbeing.viewmodel.BienestarViewModel
import com.momentum.app.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuenteEmocionalScreen(
    navController: NavController,
    viewModel: BienestarViewModel
) {
    val estado by viewModel.estado.collectAsState()

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
            Text(
                text = "Puente Emocional",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { navController.navigate(Routes.Perfil.name) }
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {}
            }
        }

        // Pregunta emocional
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "¿Cómo te sientes hoy?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                
                // Grid de emociones
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(120.dp)
                ) {
                    items(estado.estadosEmocionales) { estadoEmocional ->
                        FilterChip(
                            onClick = { 
                                viewModel.seleccionarEstadoEmocional(estadoEmocional.nombre)
                            },
                            label = { 
                                Text(
                                    text = estadoEmocional.nombre,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            selected = estadoEmocional.esSeleccionado,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Sugerencia de respiración
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Sugerencia de hoy",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "Respiración 4-4-6 • 3 repeticiones\nRespira y mira por la ventana 1 min.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Icono del árbol (placeholder)
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF4CAF50)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🌳", style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = { 
                            viewModel.iniciarEjercicioRespiracion()
                            navController.navigate(Routes.Chat.name)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        )
                    ) {
                        Text("Comenzar", color = Color.White)
                    }
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
}