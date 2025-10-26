package com.momentum.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.momentum.app.ui.screens.BienestarViewModel
import com.momentum.app.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgresoScreen(
    navController: NavController,
    viewModel: BienestarViewModel
) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progreso",
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

        // Resumen mensual simplificado
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Resumen mensual", style = MaterialTheme.typography.titleLarge)
                
                Text("Registros: ${estado.entradasDiario.size}")
                Text("Sesiones respiración: ${estado.estadisticas.sesionesRespiracion}")
                
                // Gráfico simple
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(10) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .padding(2.dp)
                                .background(Color(0xFF4CAF50), RoundedCornerShape(50))
                        )
                    }
                }
            }
        }

        // Estados frecuentes simplificado
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Estados más frecuentes", style = MaterialTheme.typography.titleLarge)
                
                if (estado.entradasDiario.isNotEmpty()) {
                    val estados = estado.entradasDiario
                        .map { it.estadoEmocional }
                        .groupingBy { it }
                        .eachCount()
                        .toList()
                        .sortedByDescending { it.second }
                        .take(3)
                    
                    estados.forEach { (estado, cantidad) ->
                        Text("$estado ($cantidad veces)")
                    }
                } else {
                    Text("No hay datos aún")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation
        BottomNavigationBar(
            currentRoute = Routes.Progreso.name,
            navController = navController
        )
    }
}