package com.momentum.app.feature_wellbeing.model

data class EstadoEmocional(
    val nombre: String,
    val esSeleccionado: Boolean = false
)

data class EntradaDiario(
    val id: String,
    val titulo: String,
    val contenido: String,
    val estadoEmocional: String,
    val fecha: String,
    val hora: String
)

data class MensajeChat(
    val id: String,
    val contenido: String,
    val esDelUsuario: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class EstadisticasProgreso(
    val registrosTotales: Int,
    val sesionesRespiracion: Int,
    val estadosMasFrecuentes: List<String>
)

data class PerfilUsuario(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val recordatoriosCada3h: Boolean = true,
    val notificacionesActivadas: Boolean = true,
    val numeroEmergencia: String = "131"
)