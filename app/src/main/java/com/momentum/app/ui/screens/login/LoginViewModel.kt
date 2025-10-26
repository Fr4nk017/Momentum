package com.momentum.app.ui.screens.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.momentum.app.model.forms.*

class LoginViewModel : ViewModel() {
    private val _form = MutableStateFlow(LoginForm())
    val form: StateFlow<LoginForm> = _form

    private val _errors = MutableStateFlow(LoginErrors())
    val errors: StateFlow<LoginErrors> = _errors

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    fun onEmailChange(v: String) { _form.update { it.copy(email = v) } }
    fun onPasswordChange(v: String) { _form.update { it.copy(password = v) } }

    fun submit(onSuccess: (String) -> Unit, onFailure: (LoginErrors) -> Unit) {
        val errs = _form.value.validate()
        _errors.value = errs
        if (errs.isValid()) {
            _userEmail.value = _form.value.email
            onSuccess(_form.value.email)
        } else {
            onFailure(errs)
        }
    }
}
