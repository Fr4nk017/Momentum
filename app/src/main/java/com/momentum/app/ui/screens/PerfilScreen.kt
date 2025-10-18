package com.momentum.app.feature_wellbeing.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun PerfilScreen(
    navController: NavController,
    viewModel: BienestarViewModel
) {
    val estado by viewModel.estado.collectAsState()
    var editando by remember { mutableStateOf(false) }
    var nombreTemp by remember { mutableStateOf(estado.perfil.nombre) }
    var apellidoTemp by remember { mutableStateOf(estado.perfil.apellido) }
    var correoTemp by remember { mutableStateOf(estado.perfil.correo) }

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
                text = "Perfil",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Información personal
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (editando) {
                    OutlinedTextField(
                        value = nombreTemp,
                        onValueChange = { nombreTemp = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = apellidoTemp,
                        onValueChange = { apellidoTemp = it },
                        label = { Text("Apellido") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = correoTemp,
                        onValueChange = { correoTemp = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                editando = false
                                nombreTemp = estado.perfil.nombre
                                apellidoTemp = estado.perfil.apellido
                                correoTemp = estado.perfil.correo
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }
                        
                        Button(
                            onClick = { 
                                viewModel.actualizarPerfil(nombreTemp, apellidoTemp, correoTemp)
                                editando = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            )
                        ) {
                            Text("Guardar", color = Color.White)
                        }
                    }
                } else {
                    Text(
                        text = "${estado.perfil.nombre} ${estado.perfil.apellido}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = estado.perfil.correo,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Button(
                        onClick = { 
                            editando = true
                            nombreTemp = estado.perfil.nombre
                            apellidoTemp = estado.perfil.apellido
                            correoTemp = estado.perfil.correo
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Editar", color = Color.White)
                    }
                }
            }
        }

        // Ajustes rápidos
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Ajustes rápidos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                // Recordatorios
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• Recordatorios cada 3 h",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Switch(
                        checked = estado.perfil.recordatoriosCada3h,
                        onCheckedChange = { viewModel.toggleRecordatorios() }
                    )
                }
                
                // Notificaciones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• Notificaciones: ${if (estado.perfil.notificacionesActivadas) "activadas" else "desactivadas"}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Switch(
                        checked = estado.perfil.notificacionesActivadas,
                        onCheckedChange = { viewModel.toggleNotificaciones() }
                    )
                }
                
                // Número de emergencia
                Text(
                    text = "• Número emergencia: ${estado.perfil.numeroEmergencia}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation
        BottomNavigationBar(
            currentRoute = Routes.Perfil.name,
            navController = navController
        )
    }
}