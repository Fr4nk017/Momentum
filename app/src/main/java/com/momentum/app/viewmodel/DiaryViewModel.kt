package com.momentum.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.data.DatabaseProvider
import com.momentum.app.data.local.DiaryEntryEntity
import com.momentum.app.data.local.MoodStatistic
import com.momentum.app.data.repository.DiaryRepository
import com.momentum.app.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DiaryUiState(
    val entries: List<DiaryEntryEntity> = emptyList(),
    val currentContent: String = "",
    val selectedMood: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val latestEntry: DiaryEntryEntity? = null,
    val totalEntries: Int = 0,
    val moodStatistics: List<MoodStatistic> = emptyList()
)

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val diaryRepository: DiaryRepository
    private val userRepository: UserRepository
    
    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()
    
    // Available mood emojis
    val availableMoods = listOf("😊", "😐", "😢", "🎉", "😡", "🤔", "😴")
    
    private var currentUserId: String = ""
    
    init {
        val db = DatabaseProvider.getDatabase(application)
        diaryRepository = DiaryRepository(db.diaryEntryDao())
        userRepository = UserRepository(db.userDao())
    }
    
    fun initializeUser(userId: String) {
        currentUserId = userId
        loadEntries()
        loadStatistics()
    }
    
    private fun loadEntries() {
        viewModelScope.launch {
            diaryRepository.getAllEntriesByUser(currentUserId).collect { entries ->
                _uiState.update { it.copy(entries = entries) }
            }
        }
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            val total = diaryRepository.getTotalEntries(currentUserId)
            val latest = diaryRepository.getLatestEntry(currentUserId)
            val stats = diaryRepository.getMoodStatistics(currentUserId)
            
            _uiState.update {
                it.copy(
                    totalEntries = total,
                    latestEntry = latest,
                    moodStatistics = stats
                )
            }
        }
    }
    
    fun updateContent(content: String) {
        _uiState.update { it.copy(currentContent = content) }
    }
    
    fun selectMood(emoji: String) {
        _uiState.update { it.copy(selectedMood = emoji) }
    }
    
    fun saveEntry() {
        val state = _uiState.value
        
        // Validation
        if (state.currentContent.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El contenido no puede estar vacío") }
            return
        }
        
        if (state.selectedMood.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Selecciona un estado de ánimo") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                val entry = DiaryEntryEntity(
                    content = state.currentContent,
                    moodEmoji = state.selectedMood,
                    userId = currentUserId
                )
                
                diaryRepository.insertEntry(entry)
                
                // Update user profile
                updateUserProfile(state.selectedMood)
                
                // Clear form
                _uiState.update {
                    it.copy(
                        currentContent = "",
                        selectedMood = "",
                        isLoading = false
                    )
                }
                
                // Reload statistics
                loadStatistics()
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al guardar: ${e.message}"
                    )
                }
            }
        }
    }
    
    private suspend fun updateUserProfile(mood: String) {
        val user = userRepository.findByEmail(currentUserId)
        user?.let {
            val updatedUser = it.copy(
                currentMood = mood,
                lastDiaryDate = System.currentTimeMillis(),
                totalEntries = it.totalEntries + 1
            )
            userRepository.update(updatedUser)
        }
    }
    
    fun deleteEntry(entry: DiaryEntryEntity) {
        viewModelScope.launch {
            try {
                diaryRepository.deleteEntry(entry)
                loadStatistics()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Error al eliminar: ${e.message}")
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
