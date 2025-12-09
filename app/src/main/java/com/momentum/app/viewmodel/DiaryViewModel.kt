package com.momentum.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.data.DatabaseProvider
import com.momentum.app.data.local.DiaryEntryEntity
import com.momentum.app.data.local.MoodStatistic
import com.momentum.app.data.repository.DiaryRepository
import com.momentum.app.data.repository.RemoteDiaryRepository
import com.momentum.app.data.repository.UserRepository
import com.momentum.app.data.remote.moods.BackendRetrofitInstance
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DiaryUiState(
    val entries: List<DiaryEntryEntity> = emptyList(),
    val currentContent: String = "",
    val selectedMood: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val latestEntry: DiaryEntryEntity? = null,
    val totalEntries: Int = 0,
    val moodStatistics: List<MoodStatistic> = emptyList(),
    val syncStatus: String = "idle"  // idle, syncing, success, error
)

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val diaryRepository: DiaryRepository
    private val remoteDiaryRepository: RemoteDiaryRepository  // Backend
    private val userRepository: UserRepository
    
    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()
    
    val availableMoods = listOf("😊", "😐", "😢", "🎉", "😡", "🤔", "😴")
    
    private var currentUserId: String = ""
    
    companion object {
        private const val TAG = "DiaryViewModel"
    }
    
    init {
        val db = DatabaseProvider.getDatabase(application)
        diaryRepository = DiaryRepository(db.diaryEntryDao())
        remoteDiaryRepository = RemoteDiaryRepository(BackendRetrofitInstance.api)
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
                _uiState.update { it.copy(isLoading = true, errorMessage = null, syncStatus = "syncing") }
                
                val entry = DiaryEntryEntity(
                    content = state.currentContent,
                    moodEmoji = state.selectedMood,
                    userId = currentUserId
                )
                
                // PASO 1: Guardar localmente (garantizado)
                val localId = diaryRepository.insertEntry(entry)
                Log.d(TAG, "Entrada guardada localmente con ID: $localId")
                
                // PASO 2: Intentar sincronizar con backend
                val title = "Entrada del ${getCurrentDate()}"
                val remoteResult = remoteDiaryRepository.enviarEntrada(
                    userId = currentUserId,
                    title = title,
                    content = state.currentContent
                )
                
                remoteResult.onSuccess { response ->
                    Log.d(TAG, "Entrada sincronizada al backend: ${response.id}")
                    _uiState.update {
                        it.copy(
                            currentContent = "",
                            selectedMood = "",
                            isLoading = false,
                            syncStatus = "success",
                            errorMessage = null
                        )
                    }
                }.onFailure { error ->
                    // Backend falló, pero ya está guardada localmente
                    Log.w(TAG, "Error en backend, pero guardada localmente: ${error.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            syncStatus = "error",
                            errorMessage = "✓ Guardado localmente\n✗ No se pudo sincronizar: ${error.message}"
                        )
                    }
                }
                
                // Actualizar perfil usuario
                updateUserProfile(state.selectedMood)
                
                // Recargar datos
                loadStatistics()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error crítico al guardar: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        syncStatus = "error",
                        errorMessage = "Error al guardar: ${e.message}"
                    )
                }
            }
        }
    }
    
    private suspend fun updateUserProfile(mood: String) {
        try {
            val user = userRepository.findByEmail(currentUserId)
            user?.let {
                val updatedUser = it.copy(
                    currentMood = mood,
                    lastDiaryDate = System.currentTimeMillis(),
                    totalEntries = it.totalEntries + 1
                )
                userRepository.update(updatedUser)
                Log.d(TAG, "Perfil de usuario actualizado")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando perfil: ${e.message}", e)
        }
    }
    
    fun deleteEntry(entry: DiaryEntryEntity) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, syncStatus = "syncing") }
                
                // PASO 1: Eliminar localmente
                diaryRepository.deleteEntry(entry)
                Log.d(TAG, "Entrada eliminada localmente: ${entry.id}")
                
                // PASO 2: Intentar eliminar en backend (si tiene ID remoto)
                entry.remoteId?.let { remoteId ->
                    val remoteResult = remoteDiaryRepository.eliminarEntrada(remoteId)
                    
                    remoteResult.onSuccess {
                        Log.d(TAG, "Entrada eliminada del backend: $remoteId")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                syncStatus = "success"
                            )
                        }
                    }.onFailure { error ->
                        Log.w(TAG, "Error eliminando del backend, pero se eliminó localmente: ${error.message}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                syncStatus = "error",
                                errorMessage = "✓ Eliminado localmente\n✗ No se pudo sincronizar"
                            )
                        }
                    }
                } ?: run {
                    // Sin ID remoto, solo se eliminó localmente
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            syncStatus = "success"
                        )
                    }
                }
                
                loadStatistics()
            } catch (e: Exception) {
                Log.e(TAG, "Error al eliminar: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        syncStatus = "error",
                        errorMessage = "Error al eliminar: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Actualiza una entrada existente en el backend y localmente.
     */
    fun updateEntry(
        entryId: String,
        remoteId: String?,
        newTitle: String,
        newContent: String
    ) {
        viewModelScope.launch {
            try {
                if (newTitle.isBlank() || newContent.isBlank()) {
                    _uiState.update { it.copy(errorMessage = "Title y content no pueden estar vacíos") }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true, syncStatus = "syncing") }

                // PASO 1: Actualizar en backend si existe remoteId
                remoteId?.let {
                    val remoteResult = remoteDiaryRepository.actualizarEntrada(
                        id = remoteId,
                        title = newTitle,
                        content = newContent
                    )

                    remoteResult.onSuccess { response ->
                        Log.d(TAG, "Entrada actualizada en backend: ${response.id}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                syncStatus = "success",
                                errorMessage = null
                            )
                        }
                    }.onFailure { error ->
                        Log.w(TAG, "Error actualizando en backend: ${error.message}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                syncStatus = "error",
                                errorMessage = "No se pudo sincronizar: ${error.message}"
                            )
                        }
                    }
                } ?: run {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            syncStatus = "idle"
                        )
                    }
                }

                loadStatistics()
            } catch (e: Exception) {
                Log.e(TAG, "Error al actualizar: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        syncStatus = "error",
                        errorMessage = "Error al actualizar: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    fun clearSyncStatus() {
        _uiState.update { it.copy(syncStatus = "idle") }
    }
    
    /**
     * Obtiene la fecha actual en formato legible (DD/MM/YYYY)
     */
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}
