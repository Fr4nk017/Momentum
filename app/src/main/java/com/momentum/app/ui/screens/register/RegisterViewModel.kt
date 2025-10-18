package com.momentum.app.ui.screens.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.momentum.app.model.forms.*

class RegisterViewModel : ViewModel() {
    private val _form = MutableStateFlow(RegisterForm())
    val form: StateFlow<RegisterForm> = _form

    private val _errors = MutableStateFlow(RegisterErrors())
    val errors: StateFlow<RegisterErrors> = _errors

    fun onNameChange(v: String) { _form.update { it.copy(name = v) } }
    fun onEmailChange(v: String) { _form.update { it.copy(email = v) } }
    fun onPasswordChange(v: String) { _form.update { it.copy(password = v) } }
    fun onConfirmPasswordChange(v: String) { _form.update { it.copy(confirmPassword = v) } }

    fun submit(onSuccess: () -> Unit, onFailure: (RegisterErrors) -> Unit) {
        val errs = _form.value.validate()
        _errors.value = errs
        if (errs.isValid()) onSuccess() else onFailure(errs)
    }
}
