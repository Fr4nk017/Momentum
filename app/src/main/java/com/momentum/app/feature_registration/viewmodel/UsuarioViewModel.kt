package com.momentum.app.feature_registration.viewmodel
import androidx.lifecycle.ViewModel
import com.momentum.app.feature_registration.model.UsuarioErrores
import com.momentum.app.feature_registration.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel  : ViewModel() {

    private val _estado = MutableStateFlow(value = UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    // Actualiza el campo correo
    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    // Actualiza el campo clave
    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    // Actualiza el campo dirección
    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    // Actualiza checkbox de aceptación
    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
            correo = validarCorreo(estadoActual.correo),
            clave = if (estadoActual.clave.length < 6) "Debe tener al menos 6 caracteres" else null,
            direccion = if (estadoActual.direccion.isBlank()) "Campo obligatorio" else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.clave,
            errores.direccion
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }

        return !hayErrores && estadoActual.aceptaTerminos
    }

    private fun validarCorreo(correo: String): String? {
        return when {
            correo.isBlank() -> "Campo obligatorio"
            !correo.contains("@") -> "Correo inválido"
            !correo.contains(".") -> "Correo inválido"
            correo.indexOf("@") == 0 -> "Correo inválido"
            correo.indexOf("@") == correo.length - 1 -> "Correo inválido"
            else -> null
        }
    }
}