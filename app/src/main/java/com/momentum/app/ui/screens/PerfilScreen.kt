package com.momentum.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.momentum.app.navigation.Routes
import com.momentum.app.navigation.AuthRoutes
import androidx.compose.ui.res.stringResource
import com.momentum.app.R

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
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

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
                text = stringResource(id = R.string.perfil_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { mostrarDialogoCerrarSesion = true }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    tint = Color.Red
                )
            }
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
                        label = { Text(stringResource(id = R.string.nombre)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = apellidoTemp,
                        onValueChange = { apellidoTemp = it },
                        label = { Text(stringResource(id = R.string.apellido)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = correoTemp,
                        onValueChange = { correoTemp = it },
                        label = { Text(stringResource(id = R.string.correo_electronico)) },
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
                            Text(stringResource(id = R.string.cancelar))
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
                            Text(stringResource(id = R.string.guardar), color = Color.White)
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
                        Text(stringResource(id = R.string.editar), color = Color.White)
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
                    text = stringResource(id = R.string.ajustes_rapidos),
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
                        text = stringResource(id = R.string.recordatorios_3h),
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
                        text = stringResource(
                            id = R.string.notificaciones_estado,
                            stringResource(
                                id = if (estado.perfil.notificacionesActivadas) R.string.notificaciones_activadas else R.string.notificaciones_desactivadas
                            )
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Switch(
                        checked = estado.perfil.notificacionesActivadas,
                        onCheckedChange = { viewModel.toggleNotificaciones() }
                    )
                }
                
                // Número de emergencia
                Text(
                    text = stringResource(id = R.string.numero_emergencia, estado.perfil.numeroEmergencia),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón de cerrar sesión
        OutlinedButton(
            onClick = { mostrarDialogoCerrarSesion = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Red
            ),
            border = BorderStroke(1.dp, Color.Red)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Cerrar sesión",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(stringResource(id = R.string.cerrar_sesion))
        }

        // Bottom Navigation
        BottomNavigationBar(
            currentRoute = Routes.Perfil.name,
            navController = navController
        )
    }

    // Diálogo de confirmación
    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            title = { Text(stringResource(id = R.string.cerrar_sesion)) },
            text = { Text(stringResource(id = R.string.confirmar_cerrar_sesion)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cerrarSesion {
                            navController.navigate(AuthRoutes.LOGIN) {
                                // Limpia el back stack hasta la ruta de login y evita duplicados
                                popUpTo(AuthRoutes.LOGIN) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        mostrarDialogoCerrarSesion = false
                    }
                ) {
                    Text(stringResource(id = R.string.cerrar_sesion), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoCerrarSesion = false }
                ) {
                    Text(stringResource(id = R.string.cancelar))
                }
            }
        )
    }
}