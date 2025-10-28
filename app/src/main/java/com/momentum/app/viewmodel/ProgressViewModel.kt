package com.momentum.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.data.DatabaseProvider
import com.momentum.app.data.local.DiaryEntryEntity
import com.momentum.app.data.model.*
import com.momentum.app.data.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ProgressUiState(
    val isLoading: Boolean = false,
    val stats: ProgressStats = ProgressStats(),
    val errorMessage: String? = null,
    val userId: String = ""
)

class ProgressViewModel(
    application: Application
) : AndroidViewModel(application) {
    
    private val progressRepository: ProgressRepository
    
    init {
        val database = DatabaseProvider.getDatabase(application)
        progressRepository = ProgressRepository(database.diaryEntryDao())
    }
    
    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()
    
    // Emojis disponibles con sus colores
    private val moodColors = mapOf(
        "😊" to 0xFF4CAF50, // Verde - Feliz
        "😐" to 0xFF9E9E9E, // Gris - Neutral
        "😢" to 0xFF2196F3, // Azul - Triste
        "🎉" to 0xFFFF9800, // Naranja - Celebración
        "😡" to 0xFFF44336, // Rojo - Enojado
        "🤔" to 0xFF9C27B0, // Púrpura - Pensativo
        "😴" to 0xFF607D8B  // Gris azulado - Cansado
    )
    
    // Mapeo de emojis a valores numéricos para cálculos
    private val moodValues = mapOf(
        "😊" to 7f,
        "🎉" to 6f,
        "😐" to 5f,
        "🤔" to 4f,
        "😴" to 3f,
        "😢" to 2f,
        "😡" to 1f
    )
    
    fun initializeUser(userId: String) {
        _uiState.value = _uiState.value.copy(userId = userId)
        loadAllStatistics()
    }
    
    private fun loadAllStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val userId = _uiState.value.userId
                
                // 1. Cargar resumen mensual
                val totalEntriesThisMonth = progressRepository.getEntriesThisMonth(userId)
                val moodStatsMonth = progressRepository.getMoodStatsThisMonth(userId)
                val predominantMood = moodStatsMonth.maxByOrNull { it.count }?.moodEmoji ?: ""
                
                // 2. Calcular rachas
                val (currentStreak, longestStreak) = progressRepository.calculateStreak(userId)
                
                // 3. Calcular distribución de estados de ánimo
                val allEntries = progressRepository.getAllEntries(userId)
                val moodDistribution = calculateMoodDistribution(allEntries)
                
                // 4. Calcular estadísticas avanzadas
                val mostActiveDayOfWeek = calculateMostActiveDay(allEntries)
                val preferredHour = calculatePreferredHour(allEntries)
                val weeklyEvolution = calculateWeeklyEvolution(allEntries)
                
                // 5. Calcular logros
                val achievements = calculateAchievements(
                    totalEntries = allEntries.size,
                    currentStreak = currentStreak,
                    longestStreak = longestStreak,
                    moodDistribution = moodDistribution
                )
                
                // TODO: Agregar sesiones de respiración cuando se implemente esa funcionalidad
                val breathingSessions = 0
                
                val stats = ProgressStats(
                    totalEntriesThisMonth = totalEntriesThisMonth,
                    predominantMood = predominantMood,
                    breathingSessions = breathingSessions,
                    consecutiveDays = currentStreak,
                    moodDistribution = moodDistribution,
                    mostActiveDayOfWeek = mostActiveDayOfWeek,
                    preferredHour = preferredHour,
                    weeklyEvolution = weeklyEvolution,
                    achievements = achievements,
                    currentStreak = currentStreak,
                    longestStreak = longestStreak
                )
                
                _uiState.value = _uiState.value.copy(
                    stats = stats,
                    isLoading = false,
                    errorMessage = null
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar estadísticas: ${e.message}"
                )
            }
        }
    }
    
    private fun calculateMoodDistribution(entries: List<DiaryEntryEntity>): Map<String, MoodStatistic> {
        if (entries.isEmpty()) return emptyMap()
        
        val moodCounts = entries.groupingBy { it.moodEmoji }.eachCount()
        val total = entries.size.toFloat()
        
        return moodCounts.mapValues { (emoji, count) ->
            MoodStatistic(
                emoji = emoji,
                count = count,
                percentage = (count / total) * 100f,
                color = moodColors[emoji] ?: 0xFF9E9E9E
            )
        }.toSortedMap(compareByDescending { moodCounts[it] ?: 0 })
    }
    
    private fun calculateMostActiveDay(entries: List<DiaryEntryEntity>): DayOfWeekStat? {
        if (entries.isEmpty()) return null
        
        val calendar = Calendar.getInstance()
        val dayNames = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
        
        val dayCount = entries
            .groupingBy { entry ->
                calendar.timeInMillis = entry.createdAt
                calendar.get(Calendar.DAY_OF_WEEK)
            }
            .eachCount()
        
        val mostActiveDay = dayCount.maxByOrNull { it.value }
        
        return mostActiveDay?.let { (dayNum, count) ->
            DayOfWeekStat(
                dayName = dayNames[dayNum - 1],
                dayNumber = dayNum,
                entryCount = count
            )
        }
    }
    
    private fun calculatePreferredHour(entries: List<DiaryEntryEntity>): HourStat? {
        if (entries.isEmpty()) return null
        
        val calendar = Calendar.getInstance()
        val hourCount = entries
            .groupingBy { entry ->
                calendar.timeInMillis = entry.createdAt
                calendar.get(Calendar.HOUR_OF_DAY)
            }
            .eachCount()
        
        val mostActiveHour = hourCount.maxByOrNull { it.value }
        
        return mostActiveHour?.let { (hour, count) ->
            val nextHour = (hour + 1) % 24
            val timeRange = String.format(
                Locale.getDefault(),
                "%02d:00 - %02d:00",
                hour,
                nextHour
            )
            
            HourStat(
                hour = hour,
                count = count,
                timeRange = timeRange
            )
        }
    }
    
    private fun calculateWeeklyEvolution(entries: List<DiaryEntryEntity>): List<WeekDayStat> {
        val calendar = Calendar.getInstance()
        val dayNames = arrayOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
        
        // Obtener los últimos 7 días
        val today = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.timeInMillis
        
        val sevenDaysAgo = Calendar.getInstance().apply {
            timeInMillis = today
            add(Calendar.DAY_OF_YEAR, -6)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        
        val recentEntries = entries.filter { it.createdAt >= sevenDaysAgo && it.createdAt <= today }
        
        // Agrupar por día
        val entriesByDay = recentEntries.groupBy { entry ->
            Calendar.getInstance().apply {
                timeInMillis = entry.createdAt
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
        
        // Crear estadísticas para cada día de la semana
        return (0..6).map { dayOffset ->
            val dayCalendar = Calendar.getInstance().apply {
                timeInMillis = today
                add(Calendar.DAY_OF_YEAR, -(6 - dayOffset))
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            
            val dayTimestamp = dayCalendar.timeInMillis
            val dayEntries = entriesByDay[dayTimestamp] ?: emptyList()
            
            val averageMood = if (dayEntries.isNotEmpty()) {
                dayEntries.mapNotNull { moodValues[it.moodEmoji] }.average().toFloat()
            } else {
                0f
            }
            
            val predominantEmoji = dayEntries
                .groupingBy { it.moodEmoji }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key ?: ""
            
            WeekDayStat(
                date = dayTimestamp,
                dayOfWeek = dayNames[dayCalendar.get(Calendar.DAY_OF_WEEK) - 1],
                averageMood = averageMood,
                entryCount = dayEntries.size,
                predominantEmoji = predominantEmoji
            )
        }
    }
    
    private fun calculateAchievements(
        totalEntries: Int,
        currentStreak: Int,
        longestStreak: Int,
        moodDistribution: Map<String, MoodStatistic>
    ): List<Achievement> {
        return listOf(
            Achievement(
                id = "first_entry",
                title = "Primera Entrada",
                description = "Escribe tu primera entrada en el diario",
                icon = "✍️",
                isUnlocked = totalEntries >= 1,
                unlockedAt = if (totalEntries >= 1) System.currentTimeMillis() else null
            ),
            Achievement(
                id = "week_warrior",
                title = "Guerrero Semanal",
                description = "Mantén una racha de 7 días consecutivos",
                icon = "🔥",
                isUnlocked = longestStreak >= 7,
                progress = (currentStreak.coerceAtMost(7) / 7f)
            ),
            Achievement(
                id = "month_master",
                title = "Maestro del Mes",
                description = "Mantén una racha de 30 días consecutivos",
                icon = "🏆",
                isUnlocked = longestStreak >= 30,
                progress = (currentStreak.coerceAtMost(30) / 30f)
            ),
            Achievement(
                id = "prolific_writer",
                title = "Escritor Prolífico",
                description = "Escribe 50 entradas en total",
                icon = "📚",
                isUnlocked = totalEntries >= 50,
                progress = (totalEntries.coerceAtMost(50) / 50f)
            ),
            Achievement(
                id = "mood_explorer",
                title = "Explorador de Emociones",
                description = "Registra todos los estados de ánimo",
                icon = "🎭",
                isUnlocked = moodDistribution.size >= 7,
                progress = (moodDistribution.size / 7f)
            ),
            Achievement(
                id = "happy_streak",
                title = "Racha de Felicidad",
                description = "Registra 5 entradas felices consecutivas",
                icon = "😊",
                isUnlocked = false, // Se calculará en una versión futura
                progress = 0f
            ),
            Achievement(
                id = "century_club",
                title = "Club de los 100",
                description = "Escribe 100 entradas en total",
                icon = "💯",
                isUnlocked = totalEntries >= 100,
                progress = (totalEntries.coerceAtMost(100) / 100f)
            ),
            Achievement(
                id = "year_legend",
                title = "Leyenda del Año",
                description = "Mantén una racha de 365 días",
                icon = "👑",
                isUnlocked = longestStreak >= 365,
                progress = (currentStreak.coerceAtMost(365) / 365f)
            )
        )
    }
    
    fun refreshStats() {
        loadAllStatistics()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
