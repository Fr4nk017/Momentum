package com.momentum.app.utils

sealed class AppError(val message: String) {
    object NetworkError : AppError("Error de conexión")
    object ValidationError : AppError("Datos inválidos")
    object UnknownError : AppError("Error desconocido")
    class CustomError(message: String) : AppError(message)
}

data class UiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null
)
