package com.momentum.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.data.DatabaseProvider
import com.momentum.app.data.local.ChatSessionEntity
import com.momentum.app.data.model.ChatMessage
import com.momentum.app.data.model.ChatResponses
import com.momentum.app.data.model.MessageType
import com.momentum.app.data.repository.ChatRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val currentInput: String = "",
    val isTyping: Boolean = false,
    val isSending: Boolean = false,
    val currentSessionId: Long? = null,
    val sessions: List<ChatSessionEntity> = emptyList(),
    val errorMessage: String? = null,
    val showQuickActions: Boolean = true,
    val breathingTimerActive: Boolean = false,
    val breathingPhase: BreathingPhase = BreathingPhase.INHALE,
    val breathingCount: Int = 0
)

enum class BreathingPhase(val duration: Int, val instruction: String) {
    INHALE(4, "Inhala profundamente"),
    HOLD(4, "Mantén el aire"),
    EXHALE(6, "Exhala lentamente")
}

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    
    private val chatRepository: ChatRepository
    
    init {
        val database = DatabaseProvider.getDatabase(application)
        chatRepository = ChatRepository(database.chatMessageDao())
    }
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private var currentUserId: String = ""
    
    fun initializeUser(userId: String) {
        currentUserId = userId
        loadOrCreateSession()
    }
    
    private fun loadOrCreateSession() {
        viewModelScope.launch {
            try {
                val session = chatRepository.getOrCreateActiveSession(currentUserId)
                _uiState.value = _uiState.value.copy(currentSessionId = session.id)
                
                // Cargar mensajes de la sesión
                chatRepository.getMessagesBySession(session.id)
                    .collect { messages ->
                        _uiState.value = _uiState.value.copy(
                            messages = messages,
                            showQuickActions = messages.isEmpty()
                        )
                    }
                
                // Si es una sesión nueva, enviar saludo
                if (session.messageCount == 0) {
                    delay(500)
                    sendBotMessage(ChatResponses.greetings.random())
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar chat: ${e.message}"
                )
            }
        }
    }
    
    fun updateInput(text: String) {
        _uiState.value = _uiState.value.copy(currentInput = text)
    }
    
    fun sendMessage() {
        val input = _uiState.value.currentInput.trim()
        if (input.isEmpty() || _uiState.value.isSending) return
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isSending = true,
                    showQuickActions = false
                )
                
                val sessionId = _uiState.value.currentSessionId ?: return@launch
                
                // Enviar mensaje del usuario
                chatRepository.sendMessage(
                    sessionId = sessionId,
                    userId = currentUserId,
                    content = input,
                    isUser = true
                )
                
                // Limpiar input
                _uiState.value = _uiState.value.copy(
                    currentInput = "",
                    isSending = false
                )
                
                // Analizar contexto emocional
                val emotionalContext = chatRepository.analyzeEmotionalContext(input)
                
                // Simular que el bot está escribiendo
                delay(1000)
                _uiState.value = _uiState.value.copy(isTyping = true)
                delay(1500)
                
                // Generar y enviar respuesta
                val response = chatRepository.generateContextualResponse(input, emotionalContext)
                sendBotMessage(response)
                
                // Ofrecer recursos adicionales según el contexto
                delay(1000)
                offerResources(emotionalContext)
                
                _uiState.value = _uiState.value.copy(isTyping = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    isTyping = false,
                    errorMessage = "Error al enviar mensaje: ${e.message}"
                )
            }
        }
    }
    
    private suspend fun sendBotMessage(
        content: String,
        messageType: MessageType = MessageType.TEXT
    ) {
        val sessionId = _uiState.value.currentSessionId ?: return
        
        chatRepository.sendMessage(
            sessionId = sessionId,
            userId = currentUserId,
            content = content,
            isUser = false,
            messageType = messageType.name
        )
    }
    
    private suspend fun offerResources(emotionalContext: String?) {
        when (emotionalContext) {
            "anxiety", "overwhelmed" -> {
                delay(1500)
                sendBotMessage(
                    "¿Te gustaría probar un ejercicio de respiración? Puede ayudarte a sentirte más calmado/a.",
                    MessageType.TEXT
                )
            }
            "sadness" -> {
                delay(1500)
                sendBotMessage(
                    "A veces escribir sobre nuestros sentimientos ayuda. ¿Te gustaría abrir tu diario?",
                    MessageType.DIARY_SUGGESTION
                )
            }
        }
    }
    
    // Acciones rápidas
    fun handleQuickAction(action: QuickAction) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(showQuickActions = false)
                
                val sessionId = _uiState.value.currentSessionId ?: return@launch
                
                // Enviar mensaje del usuario (acción seleccionada)
                chatRepository.sendMessage(
                    sessionId = sessionId,
                    userId = currentUserId,
                    content = action.userMessage,
                    isUser = true
                )
                
                delay(1000)
                _uiState.value = _uiState.value.copy(isTyping = true)
                delay(1500)
                
                // Responder según la acción
                when (action) {
                    QuickAction.CALM_ANXIETY -> {
                        sendBotMessage(ChatResponses.anxietyResponses.random())
                        delay(1000)
                        sendBotMessage(ChatResponses.breathingExercise, MessageType.BREATHING_EXERCISE)
                    }
                    QuickAction.FEELING_SAD -> {
                        sendBotMessage(ChatResponses.sadnessResponses.random())
                        delay(1000)
                        sendBotMessage(ChatResponses.reflectionPrompts.random())
                    }
                    QuickAction.WANT_REFLECT -> {
                        sendBotMessage("Excelente idea. La reflexión es una herramienta poderosa. ${ChatResponses.reflectionPrompts.random()}")
                    }
                    QuickAction.QUICK_EXERCISE -> {
                        sendBotMessage("Perfecto. Hagamos un ejercicio rápido de mindfulness.")
                        delay(1000)
                        sendBotMessage(ChatResponses.mindfulnessTip, MessageType.MEDITATION)
                    }
                    QuickAction.GROUNDING -> {
                        sendBotMessage("Vamos a conectar con el presente usando la técnica 5-4-3-2-1.")
                        delay(1000)
                        sendBotMessage(ChatResponses.groundingTechnique, MessageType.GROUNDING_TECHNIQUE)
                    }
                }
                
                _uiState.value = _uiState.value.copy(isTyping = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isTyping = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }
    
    // Ejercicio de respiración con timer
    fun startBreathingExercise() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                breathingTimerActive = true,
                breathingPhase = BreathingPhase.INHALE,
                breathingCount = 0
            )
            
            // 5 ciclos completos
            repeat(5) { cycle ->
                // Inhalar (4 segundos)
                _uiState.value = _uiState.value.copy(breathingPhase = BreathingPhase.INHALE)
                delay(4000)
                
                // Sostener (4 segundos)
                _uiState.value = _uiState.value.copy(breathingPhase = BreathingPhase.HOLD)
                delay(4000)
                
                // Exhalar (6 segundos)
                _uiState.value = _uiState.value.copy(breathingPhase = BreathingPhase.EXHALE)
                delay(6000)
                
                _uiState.value = _uiState.value.copy(breathingCount = cycle + 1)
            }
            
            _uiState.value = _uiState.value.copy(breathingTimerActive = false)
            
            // Felicitar al usuario
            delay(500)
            sendBotMessage("¡Excelente trabajo! Has completado el ejercicio. ¿Cómo te sientes ahora?")
        }
    }
    
    fun stopBreathingExercise() {
        _uiState.value = _uiState.value.copy(breathingTimerActive = false)
    }
    
    fun startNewSession() {
        viewModelScope.launch {
            try {
                // Finalizar sesión actual
                _uiState.value.currentSessionId?.let { sessionId ->
                    chatRepository.endSession(sessionId)
                }
                
                // Crear nueva sesión
                val newSessionId = chatRepository.createSession(currentUserId)
                _uiState.value = _uiState.value.copy(
                    currentSessionId = newSessionId,
                    messages = emptyList(),
                    showQuickActions = true
                )
                
                // Enviar saludo
                delay(500)
                sendBotMessage(ChatResponses.greetings.random())
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al crear nueva sesión: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

enum class QuickAction(val label: String, val userMessage: String, val icon: String) {
    CALM_ANXIETY("Necesito calmarme", "Necesito ayuda para calmar mi ansiedad", "🌊"),
    FEELING_SAD("Estoy triste", "Me siento triste y necesito apoyo", "😢"),
    WANT_REFLECT("Quiero reflexionar", "Me gustaría reflexionar sobre mis pensamientos", "💭"),
    QUICK_EXERCISE("Ejercicio rápido", "Quiero hacer un ejercicio rápido de mindfulness", "🧘"),
    GROUNDING("Técnica de grounding", "Necesito conectar con el presente", "🌍")
}
