package com.momentum.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val userId: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long,
    val messageType: String, // TEXT, BREATHING_EXERCISE, etc.
    val metadata: String? = null // JSON string
)

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val startTime: Long,
    val endTime: Long? = null,
    val userMood: String? = null,
    val topicTags: String? = null, // JSON array string
    val messageCount: Int = 0
)

// Type converters para Room
