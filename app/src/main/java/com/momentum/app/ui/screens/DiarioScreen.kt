package com.momentum.app.ui.screens

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
import androidx.compose.ui.res.stringResource
import com.momentum.app.R
import com.momentum.app.ui.animations.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiarioScreen(
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
                text = stringResource(id = R.string.diario_title),
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

        // Campo de entrada
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = estado.entradaDiarioNueva,
                    onValueChange = viewModel::onEntradaDiarioChange,
                    placeholder = { Text(stringResource(id = R.string.escribe_como_te_sientes)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )
                
                Button(
                    onClick = { viewModel.guardarEntradaDiario() },
                    enabled = estado.entradaDiarioNueva.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.guardar), color = Color.White)
                }
            }
        }

        // Lista de entradas
        if (estado.entradasDiario.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(estado.entradasDiario) { entrada ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .swipeToDismiss(
                                onDismiss = {
                                    viewModel.eliminarEntradaDiario(entrada)
                                }
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• ${entrada.titulo} — ${entrada.estadoEmocional} — ${entrada.hora}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Text(
                                text = entrada.contenido,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            // Estado vacío
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.sin_entradas_diario),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Bottom Navigation
        BottomNavigationBar(
            currentRoute = Routes.Diario.name,
            navController = navController
        )
    }
}