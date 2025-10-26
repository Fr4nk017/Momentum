package com.momentum.app.ui.screens.password

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class PasswordRecoveryForm(
    val email: String = ""
)

class PasswordRecoveryViewModel : ViewModel() {
    private val _form = MutableStateFlow(PasswordRecoveryForm())
    val form: StateFlow<PasswordRecoveryForm> = _form

    private val _isEmailSent = MutableStateFlow(false)
    val isEmailSent: StateFlow<Boolean> = _isEmailSent

    fun onEmailChange(email: String) {
        _form.update { it.copy(email = email) }
    }

    fun sendRecoveryEmail() {
        // Simular envío de email
        _isEmailSent.value = true
    }
}
