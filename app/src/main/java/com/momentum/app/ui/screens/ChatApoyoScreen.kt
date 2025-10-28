package com.momentum.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.momentum.app.data.model.ChatMessage
import com.momentum.app.data.model.MessageType
import com.momentum.app.navigation.Routes
import com.momentum.app.ui.animations.*
import com.momentum.app.viewmodel.BreathingPhase
import com.momentum.app.viewmodel.ChatViewModel
import com.momentum.app.viewmodel.QuickAction
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatApoyoScreen(
    navController: NavController,
    viewModel: BienestarViewModel,
    chatViewModel: ChatViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val chatState by chatViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Inicializar con el usuario actual
    LaunchedEffect(estado.perfil.correo) {
        if (estado.perfil.correo.isNotEmpty()) {
            chatViewModel.initializeUser(estado.perfil.correo)
        }
    }
    
    // Auto scroll al final cuando lleguen mensajes
    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatState.messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Chat de Apoyo",
                            fontWeight = FontWeight.Bold
                        )
                        if (chatState.isTyping) {
                            Text(
                                "escribiendo...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { chatViewModel.startNewSession() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Nueva conversación")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                // Input area
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    tonalElevation = 3.dp
                ) {
                    Column {
                        // Indicador de "escribiendo..."
                        AnimatedVisibility(
                            visible = chatState.isTyping,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                TypingIndicator()
                            }
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = chatState.currentInput,
                                onValueChange = { chatViewModel.updateInput(it) },
                                placeholder = { Text("Escribe cómo te sientes...") },
                                modifier = Modifier.weight(1f),
                                maxLines = 4,
                                enabled = !chatState.isSending,
                                shape = RoundedCornerShape(24.dp)
                            )
                            
                            FilledIconButton(
                                onClick = { chatViewModel.sendMessage() },
                                enabled = chatState.currentInput.isNotBlank() && !chatState.isSending,
                                modifier = Modifier.size(56.dp)
                            ) {
                                if (chatState.isSending) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Enviar"
                                    )
                                }
                            }
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
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botones de acción rápida (solo si no hay mensajes)
            if (chatState.showQuickActions) {
                item {
                    QuickActionsSection(
                        onActionSelected = { action ->
                            chatViewModel.handleQuickAction(action)
                        }
                    )
                }
            }
            
            // Mensajes del chat
            items(chatState.messages) { message ->
                ChatMessageItem(
                    message = message,
                    onStartBreathing = { chatViewModel.startBreathingExercise() },
                    onOpenDiary = { navController.navigate(Routes.Diario.name) }
                )
            }
            
            // Ejercicio de respiración activo
            if (chatState.breathingTimerActive) {
                item {
                    BreathingExerciseCard(
                        phase = chatState.breathingPhase,
                        count = chatState.breathingCount,
                        onStop = { chatViewModel.stopBreathingExercise() }
                    )
                }
            }
            
            // Espacio adicional al final
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    onActionSelected: (QuickAction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animatedSlideUp(delay = 100),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "¿Cómo puedo ayudarte hoy?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                "Selecciona una opción o escribe lo que necesites",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickAction.values().forEach { action ->
                    QuickActionButton(
                        action = action,
                        onClick = { onActionSelected(action) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    action: QuickAction,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = action.icon,
                fontSize = 24.sp
            )
            
            Text(
                text = action.label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    onStartBreathing: () -> Unit,
    onOpenDiary: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animatedFadeIn(delay = 50),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            // Avatar del bot
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (message.isUser) 20.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 20.dp
                ),
                color = if (message.isUser) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = if (!message.isUser) 3.dp else 0.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.content,
                        color = if (message.isUser)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    // Botones de acción según el tipo de mensaje
                    when (message.messageType) {
                        MessageType.BREATHING_EXERCISE -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onStartBreathing,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    Icons.Default.Spa,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Comenzar ejercicio")
                            }
                        }
                        MessageType.DIARY_SUGGESTION -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onOpenDiary,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Abrir mi diario")
                            }
                        }
                        else -> {}
                    }
                }
            }
            
            // Timestamp
            Text(
                text = formatTimestamp(message.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
        
        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            
            // Avatar del usuario
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun BreathingExerciseCard(
    phase: BreathingPhase,
    count: Int,
    onStop: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = when (phase) {
            BreathingPhase.INHALE -> 1.2f
            BreathingPhase.HOLD -> 1.2f
            BreathingPhase.EXHALE -> 0.8f
        },
        animationSpec = tween(
            durationMillis = phase.duration * 1000,
            easing = LinearEasing
        ),
        label = "breathing_scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animatedSlideUp(delay = 100),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Ejercicio de Respiración 4-4-6",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            // Círculo animado
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        when (phase) {
                            BreathingPhase.INHALE -> Color(0xFF4CAF50)
                            BreathingPhase.HOLD -> Color(0xFFFF9800)
                            BreathingPhase.EXHALE -> Color(0xFF2196F3)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = phase.instruction,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${phase.duration}s",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Text(
                "Ciclo ${count + 1} de 5",
                style = MaterialTheme.typography.bodyLarge
            )
            
            OutlinedButton(
                onClick = onStop,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Detener ejercicio")
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
        
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "typing_$index")
                    val offset by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = -10f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_$index"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(y = offset.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
