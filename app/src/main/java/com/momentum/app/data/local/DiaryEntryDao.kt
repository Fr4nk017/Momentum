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
}

data class MoodStatistic(
    val moodEmoji: String,
    val count: Int
)
