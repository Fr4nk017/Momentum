package com.momentum.app.model.validation

sealed class FormError(val message: String) {
    object Required : FormError("Campo requerido")
    object InvalidEmail : FormError("Ingresa un email válido")
    object ShortPassword : FormError("La contraseña debe tener al menos 6 caracteres")
    object PasswordsDontMatch : FormError("Las contraseñas no coinciden")
    
    // Constructor para mensajes personalizados
    class Custom(message: String) : FormError(message)
    
    // Factory function para crear errores personalizados
    companion object {
        operator fun invoke(message: String) = Custom(message)
    }
}
