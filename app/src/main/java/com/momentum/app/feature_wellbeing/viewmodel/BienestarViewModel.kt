package com.momentum.app.feature_wellbeing.viewmodel

import androidx.lifecycle.ViewModel
import com.momentum.app.feature_wellbeing.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel principal para la gestión del estado de bienestar emocional
 * 
 * Esta clase maneja toda la lógica de negocio de la aplicación Momentum,
 * incluyendo la selección de estados emocionales, gestión del diario,
 * chat de apoyo y seguimiento de progreso.
 * 
 * @author Francisco Leví Villegas
 * @since 2025-10-05
 */
class BienestarViewModel : ViewModel() {

    private val _estado = MutableStateFlow(BienestarUiState())
    val estado: StateFlow<BienestarUiState> = _estado

    fun seleccionarEstadoEmocional(nombreEstado: String) {
        _estado.update { estadoActual ->
            estadoActual.copy(
                estadoActualSeleccionado = nombreEstado,
                estadosEmocionales = estadoActual.estadosEmocionales.map { estado ->
                    estado.copy(esSeleccionado = estado.nombre == nombreEstado)
                }
            )
        }
    }

    fun onMensajeNuevoChange(mensaje: String) {
        _estado.update { it.copy(mensajeNuevo = mensaje) }
    }

    fun enviarMensaje() {
        val mensajeTexto = _estado.value.mensajeNuevo.trim()
        if (mensajeTexto.isNotBlank()) {
            val nuevoMensaje = MensajeChat(
                id = UUID.randomUUID().toString(),
                contenido = mensajeTexto,
                esDelUsuario = true
            )
            
            _estado.update { estadoActual ->
                estadoActual.copy(
                    mensajesChat = estadoActual.mensajesChat + nuevoMensaje,
                    mensajeNuevo = ""
                )
            }
            
            // Simular respuesta automática
            responderAutomaticamente(mensajeTexto)
        }
    }

    private fun responderAutomaticamente(mensajeUsuario: String) {
        val respuesta = when {
            mensajeUsuario.contains("ansioso", ignoreCase = true) -> 
                "Entiendo que te sientes ansioso. Probemos un ejercicio de respiración 4-4-6. ¿Te parece bien?"
            mensajeUsuario.contains("triste", ignoreCase = true) -> 
                "Lamento que te sientes triste. Recordemos que los sentimientos son temporales. ¿Quieres hablar de ello?"
            mensajeUsuario.contains("bien", ignoreCase = true) || mensajeUsuario.contains("feliz", ignoreCase = true) -> 
                "¡Me alegra saber que te sientes bien! Es importante celebrar estos momentos."
            else -> 
                "Gracias por compartir. ¿Te gustaría probar algún ejercicio de respiración para mantener tu bienestar?"
        }
        
        val mensajeRespuesta = MensajeChat(
            id = UUID.randomUUID().toString(),
            contenido = respuesta,
            esDelUsuario = false
        )
        
        _estado.update { estadoActual ->
            estadoActual.copy(
                mensajesChat = estadoActual.mensajesChat + mensajeRespuesta
            )
        }
    }

    fun onEntradaDiarioChange(entrada: String) {
        _estado.update { it.copy(entradaDiarioNueva = entrada) }
    }

    /**
     * Guarda una nueva entrada en el diario del usuario
     * 
     * Valida que el contenido no esté vacío y asigna automáticamente
     * la fecha, hora y estado emocional actual.
     */
    fun guardarEntradaDiario() {
        val entradaTexto = _estado.value.entradaDiarioNueva.trim()
        val estadoSeleccionado = _estado.value.estadoActualSeleccionado
        
        if (entradaTexto.isNotBlank()) {
            try {
                val ahora = Date()
                val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
                
                val nuevaEntrada = EntradaDiario(
                    id = UUID.randomUUID().toString(),
                    titulo = "Entrada ${_estado.value.entradasDiario.size + 1}",
                    contenido = entradaTexto,
                    estadoEmocional = estadoSeleccionado.ifEmpty { "Tranquilo" },
                    fecha = formatoFecha.format(ahora),
                    hora = formatoHora.format(ahora)
                )
                
                _estado.update { estadoActual ->
                    estadoActual.copy(
                        entradasDiario = listOf(nuevaEntrada) + estadoActual.entradasDiario,
                        entradaDiarioNueva = ""
                    )
                }
            } catch (e: Exception) {
                // En una app real, aquí manejaríamos el error apropiadamente
                // Por ejemplo, mostrando un Snackbar o Toast al usuario
                android.util.Log.e("BienestarViewModel", "Error al guardar entrada", e)
            }
        }
    }

    fun iniciarEjercicioRespiracion() {
        _estado.update { it.copy(ejercicioRespiracionActivo = true) }
    }

    fun detenerEjercicioRespiracion() {
        _estado.update { it.copy(ejercicioRespiracionActivo = false) }
        // Incrementar contador de sesiones
        _estado.update { estadoActual ->
            estadoActual.copy(
                estadisticas = estadoActual.estadisticas.copy(
                    sesionesRespiracion = estadoActual.estadisticas.sesionesRespiracion + 1
                )
            )
        }
    }

    fun actualizarPerfil(nombre: String, apellido: String, correo: String) {
        _estado.update { estadoActual ->
            estadoActual.copy(
                perfil = estadoActual.perfil.copy(
                    nombre = nombre,
                    apellido = apellido,
                    correo = correo
                )
            )
        }
    }

    fun toggleRecordatorios() {
        _estado.update { estadoActual ->
            estadoActual.copy(
                perfil = estadoActual.perfil.copy(
                    recordatoriosCada3h = !estadoActual.perfil.recordatoriosCada3h
                )
            )
        }
    }

    fun toggleNotificaciones() {
        _estado.update { estadoActual ->
            estadoActual.copy(
                perfil = estadoActual.perfil.copy(
                    notificacionesActivadas = !estadoActual.perfil.notificacionesActivadas
                )
            )
        }
    }
}