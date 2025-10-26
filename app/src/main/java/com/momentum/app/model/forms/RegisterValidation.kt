package com.momentum.app.model.forms

import com.momentum.app.model.validation.FormError

data class RegisterErrors(
    val name: FormError? = null,
    val email: FormError? = null,
    val password: FormError? = null,
    val confirmPassword: FormError? = null
)

fun RegisterForm.validate(): RegisterErrors {
    val nameErr = when {
        name.isBlank() -> FormError.Required
        name.length < 2 -> FormError("El nombre debe tener al menos 2 caracteres")
        !name.matches(Regex("^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ\\s]+$")) -> FormError("El nombre solo puede contener letras")
        else -> null
    }

    val emailErr = when {
        email.isBlank() -> FormError.Required
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> FormError.InvalidEmail
        email.length > 100 -> FormError("El email es demasiado largo")
        else -> null
    }

    val passErr = when {
        password.isBlank() -> FormError.Required
        password.length < 6 -> FormError.ShortPassword
        password.length > 50 -> FormError("La contraseña es demasiado larga")
        !password.matches(Regex(".*[a-zA-Z].*")) -> FormError("La contraseña debe contener al menos una letra")
        else -> null
    }

    val confirmErr = when {
        confirmPassword.isBlank() -> FormError.Required
        confirmPassword != password -> FormError.PasswordsDontMatch
        else -> null
    }

    return RegisterErrors(nameErr, emailErr, passErr, confirmErr)
}

fun RegisterErrors.isValid() = name == null && email == null && password == null && confirmPassword == null
