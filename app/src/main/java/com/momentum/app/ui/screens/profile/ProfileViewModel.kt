package com.momentum.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.domain.model.Profile
import com.momentum.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            profileRepository.getProfile()
                .catch { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
                .collect { profile ->
                    _uiState.update {
                        it.copy(
                            name = profile?.name ?: "",
                            email = profile?.email ?: "",
                            phone = profile?.phone ?: "",
                            avatarUrl = profile?.avatarUrl ?: "",
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value) }
    }

    fun onAvatarUrlChange(value: String) {
        _uiState.update { it.copy(avatarUrl = value) }
    }

    fun saveProfile() {
        val currentState = _uiState.value

        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(error = "El nombre es requerido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            profileRepository.saveProfile(
                Profile(
                    name = currentState.name.trim(),
                    email = currentState.email,
                    phone = currentState.phone.takeIf { it.isNotBlank() },
                    avatarUrl = currentState.avatarUrl.takeIf { it.isNotBlank() }
                )
            ).onSuccess {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = null,
                    isSaved = true
                ) }
            }.onFailure { error ->
                _uiState.update { it.copy(
                    isLoading = false,
                    error = error.message ?: "Error al guardar el perfil"
                ) }
            }
        }
    }

    fun clearSavedFlag() {
        _uiState.update { it.copy(isSaved = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class ProfileUiState(
    val name: String = "",
    val email: String = "", // Read-only from DataStore
    val phone: String = "",
    val avatarUrl: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaved: Boolean = false
)