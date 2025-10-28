package com.momentum.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.data.DatabaseProvider
import com.momentum.app.data.local.DiaryEntryEntity
import com.momentum.app.data.repository.DiaryRepository
import com.momentum.app.data.repository.ProgressRepository
import com.momentum.app.data.suggestions.Suggestion
import com.momentum.app.data.suggestions.SuggestionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

data class HomeUiState(
    val userId: String = "",
    val dayStreak: Int = 0,
    val longestStreak: Int = 0,
    val hasEntryToday: Boolean = false,
    val predominantMood: String = "",
    val joke: String = SuggestionManager.jokes.random(),
    val selectedMood: String? = null,
    val selectedIntensity: Int = 0,
    val suggestion: Suggestion? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val diaryRepository: DiaryRepository
    private val progressRepository: ProgressRepository

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val db = DatabaseProvider.getDatabase(application)
        diaryRepository = DiaryRepository(db.diaryEntryDao())
        progressRepository = ProgressRepository(db.diaryEntryDao())
    }

    fun initialize(userId: String) {
        _uiState.value = _uiState.value.copy(userId = userId)
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val userId = _uiState.value.userId

                // Streaks
                val (current, longest) = progressRepository.calculateStreak(userId)

                // Entry today
                val calendar = Calendar.getInstance()
                val end = calendar.timeInMillis
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                val todayEntries = progressRepository.getEntriesLastWeek(userId).filter { it.createdAt in start..end }
                val hasToday = todayEntries.isNotEmpty()

                val latest = progressRepository.getAllEntries(userId).maxByOrNull { it.createdAt }
                val mood = latest?.moodEmoji ?: ""

                _uiState.value = _uiState.value.copy(
                    dayStreak = current,
                    longestStreak = longest,
                    hasEntryToday = hasToday,
                    predominantMood = mood
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun selectMood(mood: String) {
        val intensity = _uiState.value.selectedIntensity
        val suggestion = if (intensity > 0) SuggestionManager.forMood(mood, intensity) else null
        _uiState.value = _uiState.value.copy(
            selectedMood = mood,
            suggestion = suggestion
        )
    }

    fun selectIntensity(intensity: Int) {
        val mood = _uiState.value.selectedMood
        val suggestion = if (mood != null) SuggestionManager.forMood(mood, intensity) else null
        _uiState.value = _uiState.value.copy(
            selectedIntensity = intensity,
            suggestion = suggestion
        )
    }

    fun saveQuickCheckIn(note: String? = null) {
        val mood = _uiState.value.selectedMood ?: return
        val userId = _uiState.value.userId
        val intensity = _uiState.value.selectedIntensity
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)
                diaryRepository.insertEntry(
                    DiaryEntryEntity(
                        content = note ?: "Check-in rápido ($mood, intensidad $intensity)",
                        moodEmoji = mood,
                        createdAt = System.currentTimeMillis(),
                        userId = userId
                    )
                )
                _uiState.value = _uiState.value.copy(isSaving = false)
                refresh()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = e.message)
            }
        }
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedMood = null, selectedIntensity = 0, suggestion = null)
    }

    fun clearError() { _uiState.value = _uiState.value.copy(errorMessage = null) }

    val emotionGrid: List<String> = listOf("😊","😌","⚡","😰","😢","😠","😴","🤔","🙏","🏆","❤️","😐")
}