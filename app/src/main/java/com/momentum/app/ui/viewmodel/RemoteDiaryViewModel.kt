package com.momentum.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.data.remote.diary.DiaryEntryResponse
import com.momentum.app.data.repository.RemoteDiaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DiaryUiState {
    object Idle : DiaryUiState()
    object Loading : DiaryUiState()
    data class Success(val entries: List<DiaryEntryResponse>) : DiaryUiState()
    data class Error(val message: String) : DiaryUiState()
}

class RemoteDiaryViewModel(
    private val repository: RemoteDiaryRepository,
    val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<DiaryUiState>(DiaryUiState.Idle)
    val uiState: StateFlow<DiaryUiState> = _uiState

    fun enviarEntrada(title: String, content: String) {
        viewModelScope.launch {
            try {
                _uiState.value = DiaryUiState.Loading
                repository.enviarEntrada(userId, title, content)
                // luego recargamos el historial
                val entries = repository.obtenerEntradas(userId)
                _uiState.value = DiaryUiState.Success(entries)
            } catch (e: Exception) {
                _uiState.value = DiaryUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun cargarHistorial() {
        viewModelScope.launch {
            try {
                _uiState.value = DiaryUiState.Loading
                val entries = repository.obtenerEntradas(userId)
                _uiState.value = DiaryUiState.Success(entries)
            } catch (e: Exception) {
                _uiState.value = DiaryUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
