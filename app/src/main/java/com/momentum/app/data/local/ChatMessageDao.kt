package com.momentum.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    
    @Insert
    suspend fun insertMessage(message: ChatMessageEntity): Long
    
    @Insert
    suspend fun insertSession(session: ChatSessionEntity): Long
    
    @Update
    suspend fun updateSession(session: ChatSessionEntity)
    
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesBySession(sessionId: Long): Flow<List<ChatMessageEntity>>
    
    @Query("SELECT * FROM chat_messages WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(userId: String, limit: Int = 50): List<ChatMessageEntity>
    
    @Query("SELECT * FROM chat_sessions WHERE userId = :userId ORDER BY startTime DESC")
    fun getAllSessions(userId: String): Flow<List<ChatSessionEntity>>
    
    @Query("SELECT * FROM chat_sessions WHERE userId = :userId AND endTime IS NULL LIMIT 1")
    suspend fun getActiveSession(userId: String): ChatSessionEntity?
    
    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): ChatSessionEntity?
    
    @Query("""
        UPDATE chat_sessions 
        SET messageCount = messageCount + 1 
        WHERE id = :sessionId
    """)
    suspend fun incrementMessageCount(sessionId: Long)
    
    @Query("UPDATE chat_sessions SET endTime = :endTime WHERE id = :sessionId")
    suspend fun endSession(sessionId: Long, endTime: Long)
    
    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesFromSession(sessionId: Long)
    
    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)
    
    @Query("SELECT COUNT(*) FROM chat_messages WHERE userId = :userId")
    suspend fun getTotalMessageCount(userId: String): Int
    
    @Query("""
        SELECT * FROM chat_messages 
        WHERE userId = :userId 
        AND content LIKE '%' || :keyword || '%'
        ORDER BY timestamp DESC
        LIMIT 10
    """)
    suspend fun searchMessages(userId: String, keyword: String): List<ChatMessageEntity>
}
