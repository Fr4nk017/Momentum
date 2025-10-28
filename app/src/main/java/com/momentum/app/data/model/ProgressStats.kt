package com.momentum.app.data.model

data class ProgressStats(
    // Resumen mensual
    val totalEntriesThisMonth: Int = 0,
    val predominantMood: String = "",
    val breathingSessions: Int = 0,
    val consecutiveDays: Int = 0,
    
    // Gráfico de estados de ánimo
    val moodDistribution: Map<String, MoodStatistic> = emptyMap(),
    
    // Estadísticas avanzadas
    val mostActiveDayOfWeek: DayOfWeekStat? = null,
    val preferredHour: HourStat? = null,
    val weeklyEvolution: List<WeekDayStat> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
)

data class MoodStatistic(
    val emoji: String,
    val count: Int,
    val percentage: Float,
    val color: Long // Color hex
)

data class DayOfWeekStat(
    val dayName: String,
    val dayNumber: Int, // 1 = Monday, 7 = Sunday
    val entryCount: Int
)

data class HourStat(
    val hour: Int, // 0-23
    val count: Int,
    val timeRange: String // "6:00 AM - 7:00 AM"
)

data class WeekDayStat(
    val date: Long,
    val dayOfWeek: String,
    val averageMood: Float, // 1-7 scale
    val entryCount: Int,
    val predominantEmoji: String
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val unlockedAt: Long? = null,
    val progress: Float = 0f // 0.0 to 1.0
)
