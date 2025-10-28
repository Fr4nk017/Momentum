package com.momentum.app.data.repository

import com.momentum.app.data.local.DiaryEntryDao
import com.momentum.app.data.local.DiaryEntryEntity
import com.momentum.app.data.local.MoodStatistic
import kotlinx.coroutines.flow.Flow

class DiaryRepository(private val diaryEntryDao: DiaryEntryDao) {
    
    fun getAllEntriesByUser(userId: String): Flow<List<DiaryEntryEntity>> {
        return diaryEntryDao.getAllEntriesByUser(userId)
    }
    
    suspend fun insertEntry(entry: DiaryEntryEntity): Long {
        return diaryEntryDao.insert(entry)
    }
    
    suspend fun getLatestEntry(userId: String): DiaryEntryEntity? {
        return diaryEntryDao.getLatestEntry(userId)
    }
    
    suspend fun getTotalEntries(userId: String): Int {
        return diaryEntryDao.getTotalEntries(userId)
    }
    
    suspend fun getEntryById(entryId: Long): DiaryEntryEntity? {
        return diaryEntryDao.getEntryById(entryId)
    }
    
    suspend fun deleteEntry(entry: DiaryEntryEntity) {
        diaryEntryDao.delete(entry)
    }
    
    suspend fun getMoodStatistics(userId: String): List<MoodStatistic> {
        return diaryEntryDao.getMoodStatistics(userId)
    }
}
