package com.momentum.app.feature_wellbeing.model

data class BienestarUiState(
    val estadosEmocionales: List<EstadoEmocional> = listOf(
        EstadoEmocional("Feliz"),
        EstadoEmocional("Tranquilo"),
        EstadoEmocional("Ansioso"),
        EstadoEmocional("Triste"),
        EstadoEmocional("Irritado"),
        EstadoEmocional("Con energía")
    ),
    val estadoActualSeleccionado: String = "",
    val entradasDiario: List<EntradaDiario> = emptyList(),
    val mensajesChat: List<MensajeChat> = listOf(
        MensajeChat("1", "Hola, ¿qué sientes ahora mismo?", false),
        MensajeChat("2", "Probemos este ejercicio 4-4-6 y una breve atención plena. ¿Listo para empezar?", false)
    ),
    val estadisticas: EstadisticasProgreso = EstadisticasProgreso(
        registrosTotales = 26,
        sesionesRespiracion = 14,
        estadosMasFrecuentes = listOf("Tranquilo", "Feliz", "Ansioso", "Con energía")
    ),
    val perfil: PerfilUsuario = PerfilUsuario(
        nombre = "Nombre",
        apellido = "Apellido", 
        correo = "correo@dominio.com"
    ),
    val mensajeNuevo: String = "",
    val entradaDiarioNueva: String = "",
    val ejercicioRespiracionActivo: Boolean = false
)