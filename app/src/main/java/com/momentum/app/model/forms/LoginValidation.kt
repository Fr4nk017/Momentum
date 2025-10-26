package com.momentum.app.model.forms

import com.momentum.app.model.validation.FormError

data class LoginErrors(
    val email: FormError? = null,
    val password: FormError? = null
)

fun LoginForm.validate(): LoginErrors {
    val emailErr =
        if (email.isBlank()) FormError.Required
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            FormError.InvalidEmail else null

    val passErr =
        if (password.isBlank()) FormError.Required
        else if (password.length < 6) FormError.ShortPassword else null

    return LoginErrors(emailErr, passErr)
}

fun LoginErrors.isValid() = email == null && password == null
