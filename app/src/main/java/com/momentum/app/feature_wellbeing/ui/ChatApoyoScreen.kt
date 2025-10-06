package com.momentum.app.feature_wellbeing.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
import kotlinx.coroutines.launch

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
                    text = "Chat de apoyo",
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
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = estado.mensajeNuevo,
                    onValueChange = viewModel::onMensajeNuevoChange,
                    placeholder = { Text("Escribe un mensaje...") },
                    modifier = Modifier.weight(1f),
                    maxLines = 3
                )
                
                IconButton(
                    onClick = { viewModel.enviarMensaje() },
                    enabled = estado.mensajeNuevo.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Enviar"
                    )
                }
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
        val fases = listOf("Inhala" to 4, "Mantén" to 4, "Exhala" to 6)
        var faseIndex = 0
        
        while (ciclo <= 3) {
            val (nombreFase, duracion) = fases[faseIndex]
            fase = nombreFase
            
            for (i in duracion downTo 1) {
                contador = i
                kotlinx.coroutines.delay(1000)
            }
            
            faseIndex = (faseIndex + 1) % fases.size
            if (faseIndex == 0) ciclo++
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Ejercicio de Respiración 4-4-6",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = fase,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = contador.toString(),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            
            Text(
                text = "Ciclo $ciclo de 3",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (ciclo > 3) {
                Text(
                    text = "¡Ejercicio completado! ✅",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}