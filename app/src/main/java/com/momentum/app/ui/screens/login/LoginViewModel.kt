package com.momentum.app.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.momentum.app.model.forms.*
import com.momentum.app.data.DatabaseProvider

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val _form = MutableStateFlow(LoginForm())
    val form: StateFlow<LoginForm> = _form

    private val _errors = MutableStateFlow(LoginErrors())
    val errors: StateFlow<LoginErrors> = _errors

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val repository by lazy { DatabaseProvider.clientRepository(getApplication()) }

    fun onEmailChange(v: String) { _form.update { it.copy(email = v) } }
    fun onPasswordChange(v: String) { _form.update { it.copy(password = v) } }

    fun submit(onSuccess: (String) -> Unit, onFailure: (LoginErrors) -> Unit) {
        val errs = _form.value.validate()
        _errors.value = errs
        if (errs.isValid()) {
            val email = _form.value.email
            _isLoading.value = true
            // Verificar que el usuario exista en la base de datos
            viewModelScope.launch {
                runCatching { repository.getByEmail(email) }
                    .onSuccess { entity ->
                        _isLoading.value = false
                        if (entity != null) {
                            _userEmail.value = email
                            onSuccess(email)
                        } else {
                            // Usuario no registrado
                            _errors.value = LoginErrors(
                                email = com.momentum.app.model.validation.FormError.Custom("Este correo no está registrado")
                            )
                            onFailure(_errors.value)
                        }
                    }
                    .onFailure {
                        _isLoading.value = false
                        // Error al consultar BD
                        _errors.value = LoginErrors(
                            email = com.momentum.app.model.validation.FormError.Custom("Error al verificar usuario")
                        )
                        onFailure(_errors.value)
                    }
            }
        } else {
            onFailure(errs)
        }
    }
}
