package com.momentum.app.data.repository

import com.momentum.app.data.local.ChatMessageDao
import com.momentum.app.data.local.ChatMessageEntity
import com.momentum.app.data.local.ChatSessionEntity
import com.momentum.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(
    private val chatMessageDao: ChatMessageDao
) {
    
    // Sesiones
    suspend fun createSession(userId: String, userMood: String? = null): Long {
        val session = ChatSessionEntity(
            userId = userId,
            startTime = System.currentTimeMillis(),
            userMood = userMood
        )
        return chatMessageDao.insertSession(session)
    }
    
    suspend fun getOrCreateActiveSession(userId: String): ChatSessionEntity {
        return chatMessageDao.getActiveSession(userId) 
            ?: run {
                val sessionId = createSession(userId)
                chatMessageDao.getSessionById(sessionId)!!
            }
    }
    
    suspend fun endSession(sessionId: Long) {
        chatMessageDao.endSession(sessionId, System.currentTimeMillis())
    }
    
    fun getAllSessions(userId: String): Flow<List<ChatSessionEntity>> {
        return chatMessageDao.getAllSessions(userId)
    }
    
    // Mensajes
    suspend fun sendMessage(
        sessionId: Long,
        userId: String,
        content: String,
        isUser: Boolean,
        messageType: String = "TEXT",
        metadata: String? = null
    ): Long {
        val message = ChatMessageEntity(
            sessionId = sessionId,
            userId = userId,
            content = content,
            isUser = isUser,
            timestamp = System.currentTimeMillis(),
            messageType = messageType,
            metadata = metadata
        )
        
        val messageId = chatMessageDao.insertMessage(message)
        chatMessageDao.incrementMessageCount(sessionId)
        
        return messageId
    }
    
    fun getMessagesBySession(sessionId: Long): Flow<List<ChatMessage>> {
        return chatMessageDao.getMessagesBySession(sessionId).map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id,
                    content = entity.content,
                    isUser = entity.isUser,
                    timestamp = entity.timestamp,
                    messageType = MessageType.valueOf(entity.messageType),
                    metadata = entity.metadata?.let { 
                        // Parse JSON metadata if needed
                        null // Simplificado por ahora
                    }
                )
            }
        }
    }
    
    suspend fun getRecentMessages(userId: String, limit: Int = 50): List<ChatMessage> {
        return chatMessageDao.getRecentMessages(userId, limit).map { entity ->
            ChatMessage(
                id = entity.id,
                content = entity.content,
                isUser = entity.isUser,
                timestamp = entity.timestamp,
                messageType = MessageType.valueOf(entity.messageType)
            )
        }
    }
    
    suspend fun searchMessages(userId: String, keyword: String): List<ChatMessage> {
        return chatMessageDao.searchMessages(userId, keyword).map { entity ->
            ChatMessage(
                id = entity.id,
                content = entity.content,
                isUser = entity.isUser,
                timestamp = entity.timestamp,
                messageType = MessageType.valueOf(entity.messageType)
            )
        }
    }
    
    // Análisis de contexto
    fun analyzeEmotionalContext(message: String): String? {
        val lowerMessage = message.lowercase()
        
        return when {
            EmotionalKeywords.anxiety.any { lowerMessage.contains(it) } -> "anxiety"
            EmotionalKeywords.sadness.any { lowerMessage.contains(it) } -> "sadness"
            EmotionalKeywords.anger.any { lowerMessage.contains(it) } -> "anger"
            EmotionalKeywords.overwhelmed.any { lowerMessage.contains(it) } -> "overwhelmed"
            EmotionalKeywords.positive.any { lowerMessage.contains(it) } -> "positive"
            else -> null
        }
    }
    
    // Generar respuesta contextual
    fun generateContextualResponse(userMessage: String, emotionalContext: String?): String {
        return when (emotionalContext) {
            "anxiety" -> ChatResponses.anxietyResponses.random()
            "sadness" -> ChatResponses.sadnessResponses.random()
            "anger", "overwhelmed" -> ChatResponses.anxietyResponses.random()
            "positive" -> ChatResponses.encouragement.random()
            else -> {
                // Respuesta genérica empática
                val genericResponses = listOf(
                    "Entiendo. ¿Podrías contarme más sobre eso?",
                    "Te escucho. ¿Qué sientes en este momento?",
                    "Gracias por compartir esto conmigo. ¿Cómo te gustaría que te apoyara?",
                    ChatResponses.reflectionPrompts.random()
                )
                genericResponses.random()
            }
        }
    }
    
    suspend fun deleteSession(sessionId: Long) {
        chatMessageDao.deleteMessagesFromSession(sessionId)
        chatMessageDao.deleteSession(sessionId)
    }
}
