package com.momentum.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.data.remote.moods.MoodEntryResponse
import com.momentum.app.data.repository.MoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MoodUiState {
    object Idle : MoodUiState()
    object Loading : MoodUiState()
    data class Success(val moods: List<MoodEntryResponse>) : MoodUiState()
    data class Error(val message: String) : MoodUiState()
}

class MoodViewModel(
    private val repository: MoodRepository,
    val userId: String // podrías sacarlo de DataStore o del login
) : ViewModel() {

    private val _uiState = MutableStateFlow<MoodUiState>(MoodUiState.Idle)
    val uiState: StateFlow<MoodUiState> = _uiState

    fun enviarMood(emotion: String, note: String?) {
        viewModelScope.launch {
            try {
                _uiState.value = MoodUiState.Loading
                repository.enviarMood(userId, emotion, note)
                // luego recargamos el historial
                val moods = repository.obtenerHistorial(userId)
                _uiState.value = MoodUiState.Success(moods)
            } catch (e: Exception) {
                _uiState.value = MoodUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun cargarHistorial() {
        viewModelScope.launch {
            try {
                _uiState.value = MoodUiState.Loading
                val moods = repository.obtenerHistorial(userId)
                _uiState.value = MoodUiState.Success(moods)
            } catch (e: Exception) {
                _uiState.value = MoodUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
