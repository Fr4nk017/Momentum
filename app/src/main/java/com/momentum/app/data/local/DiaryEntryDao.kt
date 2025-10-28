package com.momentum.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryEntryDao {
    @Insert
    suspend fun insert(entry: DiaryEntryEntity): Long

    @Query("SELECT * FROM diary_entries WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllEntriesByUser(userId: String): Flow<List<DiaryEntryEntity>>

    @Query("SELECT * FROM diary_entries WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestEntry(userId: String): DiaryEntryEntity?

    @Query("SELECT COUNT(*) FROM diary_entries WHERE userId = :userId")
    suspend fun getTotalEntries(userId: String): Int

    @Query("SELECT * FROM diary_entries WHERE id = :entryId")
    suspend fun getEntryById(entryId: Long): DiaryEntryEntity?

    @Delete
    suspend fun delete(entry: DiaryEntryEntity)

    @Query("SELECT moodEmoji, COUNT(*) as count FROM diary_entries WHERE userId = :userId GROUP BY moodEmoji ORDER BY count DESC")
    suspend fun getMoodStatistics(userId: String): List<MoodStatistic>
    
    // Consultas para Analytics Avanzados
    
    @Query("""
        SELECT COUNT(*) FROM diary_entries 
        WHERE userId = :userId 
        AND createdAt >= :startOfMonth 
        AND createdAt <= :endOfMonth
    """)
    suspend fun getEntriesCountInMonth(userId: String, startOfMonth: Long, endOfMonth: Long): Int
    
    @Query("""
        SELECT moodEmoji, COUNT(*) as count 
        FROM diary_entries 
        WHERE userId = :userId 
        AND createdAt >= :startOfMonth 
        AND createdAt <= :endOfMonth
        GROUP BY moodEmoji 
        ORDER BY count DESC
    """)
    suspend fun getMoodStatisticsForMonth(userId: String, startOfMonth: Long, endOfMonth: Long): List<MoodStatistic>
    
    @Query("""
        SELECT * FROM diary_entries 
        WHERE userId = :userId 
        AND createdAt >= :startDate 
        AND createdAt <= :endDate
        ORDER BY createdAt ASC
    """)
    suspend fun getEntriesInDateRange(userId: String, startDate: Long, endDate: Long): List<DiaryEntryEntity>
    
    @Query("""
        SELECT * FROM diary_entries 
        WHERE userId = :userId 
        ORDER BY createdAt DESC
    """)
    suspend fun getAllEntriesList(userId: String): List<DiaryEntryEntity>
}

data class MoodStatistic(
    val moodEmoji: String,
    val count: Int
)
