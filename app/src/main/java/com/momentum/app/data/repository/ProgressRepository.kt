package com.momentum.app.data.repository

import com.momentum.app.data.local.DiaryEntryDao
import com.momentum.app.data.local.DiaryEntryEntity
import com.momentum.app.data.local.MoodStatistic
import java.util.*

class ProgressRepository(
    private val diaryEntryDao: DiaryEntryDao
) {
    
    // Obtener todas las entradas del usuario
    suspend fun getAllEntries(userId: String): List<DiaryEntryEntity> {
        return diaryEntryDao.getAllEntriesList(userId)
    }
    
    // Obtener entradas del mes actual
    suspend fun getEntriesThisMonth(userId: String): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.timeInMillis
        
        return diaryEntryDao.getEntriesCountInMonth(userId, startOfMonth, endOfMonth)
    }
    
    // Obtener estadísticas de estados de ánimo del mes
    suspend fun getMoodStatsThisMonth(userId: String): List<MoodStatistic> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.timeInMillis
        
        return diaryEntryDao.getMoodStatisticsForMonth(userId, startOfMonth, endOfMonth)
    }
    
    // Obtener entradas de los últimos 7 días
    suspend fun getEntriesLastWeek(userId: String): List<DiaryEntryEntity> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis
        
        return diaryEntryDao.getEntriesInDateRange(userId, startDate, endDate)
    }
    
    // Calcular racha consecutiva de días
    suspend fun calculateStreak(userId: String): Pair<Int, Int> {
        val allEntries = diaryEntryDao.getAllEntriesList(userId)
        if (allEntries.isEmpty()) return Pair(0, 0)
        
        // Ordenar por fecha descendente
        val sortedEntries = allEntries.sortedByDescending { it.createdAt }
        
        val calendar = Calendar.getInstance()
        val today = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 0
        var lastDate: Long? = null
        
        val uniqueDays = sortedEntries
            .map { entry ->
                Calendar.getInstance().apply {
                    timeInMillis = entry.createdAt
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
            .distinct()
            .sorted()
            .reversed()
        
        // Calcular racha actual
        for ((index, dayTimestamp) in uniqueDays.withIndex()) {
            val expectedDay = Calendar.getInstance().apply {
                timeInMillis = today
                add(Calendar.DAY_OF_YEAR, -index)
            }.timeInMillis
            
            if (dayTimestamp == expectedDay) {
                currentStreak++
            } else {
                break
            }
        }
        
        // Calcular racha más larga
        for (dayTimestamp in uniqueDays) {
            if (lastDate == null) {
                tempStreak = 1
            } else {
                val diff = (lastDate - dayTimestamp) / (1000 * 60 * 60 * 24)
                if (diff == 1L) {
                    tempStreak++
                } else {
                    if (tempStreak > longestStreak) {
                        longestStreak = tempStreak
                    }
                    tempStreak = 1
                }
            }
            lastDate = dayTimestamp
        }
        
        if (tempStreak > longestStreak) {
            longestStreak = tempStreak
        }
        
        return Pair(currentStreak, longestStreak)
    }
}
