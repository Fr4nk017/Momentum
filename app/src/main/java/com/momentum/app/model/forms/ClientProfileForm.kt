package com.momentum.app.model.forms

data class ClientProfileForm(
    val nombre: String = "",
    val edad: Int? = null,
    val sexo: String = "",
    val estadoCivil: String = "",
    val ocupacion: String = "",
    val email: String = "",
    val telefono: String = ""
)

data class ClientProfileErrors(
    val nombre: String? = null,
    val edad: String? = null,
    val sexo: String? = null,
    val estadoCivil: String? = null,
    val ocupacion: String? = null,
    val email: String? = null,
    val telefono: String? = null
)

fun ClientProfileForm.validate(): ClientProfileErrors {
    fun String.isEmail(): Boolean =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}").matches(this)

    fun String.onlyDigits(): Boolean = this.all { it.isDigit() }

    val nombreError = if (nombre.trim().isEmpty()) "El nombre es obligatorio" else null

    val edadError = when {
        edad == null -> null // opcional
        edad < 0 -> "La edad no puede ser negativa"
        edad > 120 -> "La edad no es válida"
        else -> null
    }

    val sexoError = if (sexo.isNotEmpty()) {
        val opciones = setOf("Masculino", "Femenino", "Otro")
        if (sexo !in opciones) "Sexo inválido (use: Masculino, Femenino u Otro)" else null
    } else null

    val estadoCivilError = if (estadoCivil.length > 40) "Estado civil demasiado largo" else null

    val ocupacionError = if (ocupacion.length > 60) "Ocupación demasiado larga" else null

    val emailError = if (email.isNotEmpty() && !email.isEmail()) "Correo electrónico inválido" else null

    val telefonoError = if (telefono.isNotEmpty()) {
        when {
            !telefono.onlyDigits() -> "El teléfono debe contener solo números"
            telefono.length < 7 || telefono.length > 15 -> "El teléfono debe tener entre 7 y 15 dígitos"
            else -> null
        }
    } else null

    return ClientProfileErrors(
        nombre = nombreError,
        edad = edadError,
        sexo = sexoError,
        estadoCivil = estadoCivilError,
        ocupacion = ocupacionError,
        email = emailError,
        telefono = telefonoError
    )
}

fun ClientProfileErrors.isValid(): Boolean =
    listOf(nombre, edad, sexo, estadoCivil, ocupacion, email, telefono).all { it == null }
