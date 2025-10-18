package com.momentum.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.momentum.app.model.forms.ClientProfileForm

/**
 * ViewModel para manejar el formulario de perfil del cliente.
 */
class ClientProfileViewModel : ViewModel() {
    private val _form = MutableStateFlow(ClientProfileForm())
    val form: StateFlow<ClientProfileForm> = _form

    private val _errors = MutableStateFlow<Map<String, String>>(emptyMap())
    val errors: StateFlow<Map<String, String>> = _errors

    fun onChange(field: String, value: String) {
        when (field) {
            "nombre" -> _form.update { it.copy(nombre = value) }
            "edad" -> _form.update { it.copy(edad = value.toIntOrNull()) }
            "sexo" -> _form.update { it.copy(sexo = value) }
            "estadoCivil" -> _form.update { it.copy(estadoCivil = value) }
            "ocupacion" -> _form.update { it.copy(ocupacion = value) }
            "email" -> _form.update { it.copy(email = value) }
            "telefono" -> _form.update { it.copy(telefono = value) }
        }
    }

    fun validate(): Boolean {
        val errs = mutableMapOf<String, String>()
        val value = form.value
        if (value.nombre.isBlank()) errs["nombre"] = "Campo obligatorio"
        if (value.edad == null || value.edad <= 0) errs["edad"] = "Debe ingresar una edad válida"
        if (value.email.isBlank()) errs["email"] = "Campo obligatorio"
        _errors.value = errs
        return errs.isEmpty()
    }
}
