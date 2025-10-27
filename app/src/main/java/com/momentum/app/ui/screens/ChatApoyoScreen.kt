package com.momentum.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.momentum.app.R
import com.momentum.app.ui.animations.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatApoyoScreen(
    navController: NavController,
    viewModel: BienestarViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(estado.mensajesChat.size) {
        if (estado.mensajesChat.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(estado.mensajesChat.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.chat_apoyo_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .animatedFadeIn(delay = 0)
                        .animatedScale(delay = 0)
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
        }

        // Chat messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(estado.mensajesChat) { mensaje ->
                MessageBubble(
                    mensaje = mensaje.contenido,
                    esDelUsuario = mensaje.esDelUsuario
                )
            }
            
            // Ejercicio de respiración si está activo
            if (estado.ejercicioRespiracionActivo) {
                item {
                    EjercicioRespiracionCard(
                        onCompletar = { viewModel.detenerEjercicioRespiracion() }
                    )
                }
            }
        }

        // Input area
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .animatedSlideUp(delay = 120),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = estado.mensajeNuevo,
                    onValueChange = viewModel::onMensajeNuevoChange,
                    placeholder = { Text(stringResource(id = R.string.escribe_mensaje)) },
                    modifier = Modifier.weight(1f),
                    maxLines = 3
                )
                
                IconButton(
                    onClick = { viewModel.enviarMensaje() },
                    enabled = estado.mensajeNuevo.isNotBlank()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(id = R.string.desc_enviar)
                    )
                }
            }
        }

        // Botones de respuesta rápida motivacional
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { 
                    viewModel.onMensajeNuevoChange("Necesito motivación")
                    viewModel.enviarMensaje()
                },
                modifier = Modifier
                    .weight(1f)
                    .bounceClick(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Motivación", color = Color.White)
            }
            
            Button(
                onClick = { 
                    viewModel.onMensajeNuevoChange("Dame un consejo")
                    viewModel.enviarMensaje()
                },
                modifier = Modifier
                    .weight(1f)
                    .bounceClick(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text("Consejo", color = Color.White)
            }
        }

        // Bottom Navigation
        BottomNavigationBar(
            currentRoute = Routes.Chat.name,
            navController = navController
        )
    }
}

@Composable
fun MessageBubble(
    mensaje: String,
    esDelUsuario: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (esDelUsuario) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (esDelUsuario) 16.dp else 4.dp,
                bottomEnd = if (esDelUsuario) 4.dp else 16.dp
            ),
            color = if (esDelUsuario) Color.Black else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = mensaje,
                modifier = Modifier.padding(12.dp),
                color = if (esDelUsuario) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun EjercicioRespiracionCard(
    onCompletar: () -> Unit
) {
    var fase by remember { mutableStateOf("Inhala") }
    var contador by remember { mutableStateOf(4) }
    var ciclo by remember { mutableStateOf(1) }
    
    LaunchedEffect(Unit) {
        repeat(3) { // 3 ciclos
            // Inhalar
            fase = "Inhala"
            for (i in 4 downTo 1) {
                contador = i
                kotlinx.coroutines.delay(1000)
            }
            
            // Mantener
            fase = "Mantén"
            for (i in 4 downTo 1) {
                contador = i
                kotlinx.coroutines.delay(1000)
            }
            
            // Exhalar
            fase = "Exhala"
            for (i in 6 downTo 1) {
                contador = i
                kotlinx.coroutines.delay(1000)
            }
            
            ciclo++
        }
        
        onCompletar()
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Ejercicio de Respiración 4-4-6")
            Text(fase, style = MaterialTheme.typography.headlineMedium)
            Text(contador.toString(), style = MaterialTheme.typography.displayLarge)
            Text("Ciclo $ciclo de 3")
            
            if (ciclo > 3) {
                Text("¡Completado! ✅")
            }
        }
    }
}