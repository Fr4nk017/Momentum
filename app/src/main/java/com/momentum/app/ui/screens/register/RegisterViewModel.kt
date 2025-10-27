package com.momentum.app.ui.screens.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.data.DatabaseProvider
import com.momentum.app.data.repository.ClientRepository
import com.momentum.app.model.forms.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val _form = MutableStateFlow(RegisterForm())
    val form: StateFlow<RegisterForm> = _form

    private val _errors = MutableStateFlow(RegisterErrors())
    val errors: StateFlow<RegisterErrors> = _errors

    fun onNameChange(v: String) { _form.update { it.copy(name = v) } }
    fun onEmailChange(v: String) { _form.update { it.copy(email = v) } }
    fun onPasswordChange(v: String) { _form.update { it.copy(password = v) } }
    fun onConfirmPasswordChange(v: String) { _form.update { it.copy(confirmPassword = v) } }

    private val repository: ClientRepository by lazy {
        DatabaseProvider.clientRepository(getApplication())
    }

    fun submit(onSuccess: (String, String, String) -> Unit, onFailure: (RegisterErrors) -> Unit) {
        val errs = _form.value.validate()
        _errors.value = errs
        if (errs.isValid()) {
            val name = _form.value.name
            val email = _form.value.email
            viewModelScope.launch {
                runCatching { repository.insert(name = name, email = email) }
                    .onFailure { /* log or set an error state if desired */ }
            }
            onSuccess(email, name, "")
        } else {
            onFailure(errs)
        }
    }
}
