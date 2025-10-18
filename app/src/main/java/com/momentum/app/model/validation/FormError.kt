package com.momentum.app.model.validation

sealed class FormError(val message: String) {
    object Required : FormError("Campo requerido")
    object InvalidEmail : FormError("Email inválido")
    object ShortPassword : FormError("La contraseña es muy corta")
    object PasswordsDontMatch : FormError("Las contraseñas no coinciden")
}
