package com.momentum.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Terrain
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
import com.momentum.app.ui.animations.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.momentum.app.model.forms.ClientProfileForm
import com.momentum.app.model.forms.validate
import com.momentum.app.model.forms.isValid

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
    var edadTemp by remember { mutableStateOf(estado.perfil.edad?.toString() ?: "") }
    var sexoTemp by remember { mutableStateOf(estado.perfil.sexo) }
    var estadoCivilTemp by remember { mutableStateOf(estado.perfil.estadoCivil) }
    var ocupacionTemp by remember { mutableStateOf(estado.perfil.ocupacion) }
    var telefonoTemp by remember { mutableStateOf(estado.perfil.telefono) }
    val form by derivedStateOf {
        ClientProfileForm(
            nombre = nombreTemp,
            edad = edadTemp.toIntOrNull(),
            sexo = sexoTemp,
            estadoCivil = estadoCivilTemp,
            ocupacion = ocupacionTemp,
            email = correoTemp,
            telefono = telefonoTemp
        )
    }
    val errores = form.validate()
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás"
                    )
                }
                Text(
                    text = stringResource(id = R.string.perfil_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .animatedFadeIn(delay = 0)
                        .animatedScale(delay = 0)
                )
            }
            
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
            modifier = Modifier
                .fillMaxWidth()
                .animatedSlideUp(delay = 120)
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
                        modifier = Modifier.fillMaxWidth(),
                        isError = errores.nombre != null,
                        supportingText = {
                            errores.nombre?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
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
                        modifier = Modifier.fillMaxWidth(),
                        isError = errores.email != null,
                        supportingText = {
                            errores.email?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    OutlinedTextField(
                        value = edadTemp,
                        onValueChange = { edadTemp = it.filter { ch -> ch.isDigit() }.take(3) },
                        label = { Text("Edad (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = errores.edad != null,
                        supportingText = {
                            errores.edad?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    OutlinedTextField(
                        value = sexoTemp,
                        onValueChange = { sexoTemp = it },
                        label = { Text("Sexo (Masculino/Femenino/Otro)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errores.sexo != null,
                        supportingText = {
                            errores.sexo?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    OutlinedTextField(
                        value = estadoCivilTemp,
                        onValueChange = { estadoCivilTemp = it },
                        label = { Text("Estado civil") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errores.estadoCivil != null,
                        supportingText = {
                            errores.estadoCivil?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    OutlinedTextField(
                        value = ocupacionTemp,
                        onValueChange = { ocupacionTemp = it },
                        label = { Text("Ocupación") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errores.ocupacion != null,
                        supportingText = {
                            errores.ocupacion?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    OutlinedTextField(
                        value = telefonoTemp,
                        onValueChange = { telefonoTemp = it.filter { ch -> ch.isDigit() }.take(15) },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = errores.telefono != null,
                        supportingText = {
                            errores.telefono?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
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
                                edadTemp = estado.perfil.edad?.toString() ?: ""
                                sexoTemp = estado.perfil.sexo
                                estadoCivilTemp = estado.perfil.estadoCivil
                                ocupacionTemp = estado.perfil.ocupacion
                                telefonoTemp = estado.perfil.telefono
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.cancelar))
                        }
                        
                        Button(
                            onClick = { 
                                // Actualiza nombre/apellido/correo y campos extendidos si es válido
                                viewModel.actualizarPerfil(nombreTemp, apellidoTemp, correoTemp)
                                viewModel.actualizarPerfilCliente(form)
                                editando = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            ),
                            enabled = errores.isValid()
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

                    // Información adicional
                    val infoAdicional = listOfNotNull(
                        estado.perfil.edad?.let { "Edad: $it" },
                        estado.perfil.sexo.takeIf { it.isNotBlank() }?.let { "Sexo: $it" },
                        estado.perfil.estadoCivil.takeIf { it.isNotBlank() }?.let { "Estado civil: $it" },
                        estado.perfil.ocupacion.takeIf { it.isNotBlank() }?.let { "Ocupación: $it" },
                        estado.perfil.telefono.takeIf { it.isNotBlank() }?.let { "Teléfono: $it" }
                    )
                    infoAdicional.forEach { dato ->
                        Text(
                            text = dato,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Button(
                        onClick = { 
                            editando = true
                            nombreTemp = estado.perfil.nombre
                            apellidoTemp = estado.perfil.apellido
                            correoTemp = estado.perfil.correo
                            edadTemp = estado.perfil.edad?.toString() ?: ""
                            sexoTemp = estado.perfil.sexo
                            estadoCivilTemp = estado.perfil.estadoCivil
                            ocupacionTemp = estado.perfil.ocupacion
                            telefonoTemp = estado.perfil.telefono
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .bounceClick()
                    ) {
                        Text(stringResource(id = R.string.editar), color = Color.White)
                    }
                }
            }
        }

        // Ajustes rápidos
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

                // Modo oscuro
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Modo oscuro",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Switch(
                        checked = estado.perfil.modoOscuro,
                        onCheckedChange = { viewModel.toggleModoOscuro() }
                    )
                }

                Button(
                    onClick = { navController.navigate(Routes.Places.name) },
                    modifier = Modifier.fillMaxWidth().bounceClick(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Explorar lugares cercanos", color = Color.White)
                }
                
                Button(
                    onClick = { navController.navigate(Routes.HikingHistory.name) },
                    modifier = Modifier.fillMaxWidth().bounceClick(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Icon(
                        imageVector = Icons.Default.Terrain,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Historial de senderismo", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón de cerrar sesión
        OutlinedButton(
            onClick = { mostrarDialogoCerrarSesion = true },
            modifier = Modifier
                .fillMaxWidth()
                .bounceClick(),
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