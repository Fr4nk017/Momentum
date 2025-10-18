package com.momentum.app.model.forms

import com.momentum.app.model.validation.FormError

data class RegisterErrors(
    val name: FormError? = null,
    val email: FormError? = null,
    val password: FormError? = null,
    val confirmPassword: FormError? = null
)

fun RegisterForm.validate(): RegisterErrors {
    val nameErr = if (name.isBlank()) FormError.Required else null

    val emailErr =
        if (email.isBlank()) FormError.Required
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            FormError.InvalidEmail else null

    val passErr =
        if (password.isBlank()) FormError.Required
        else if (password.length < 6) FormError.ShortPassword else null

    val confirmErr =
        if (confirmPassword.isBlank()) FormError.Required
        else if (confirmPassword != password) FormError.PasswordsDontMatch else null

    return RegisterErrors(nameErr, emailErr, passErr, confirmErr)
}

fun RegisterErrors.isValid() = name == null && email == null && password == null && confirmPassword == null
