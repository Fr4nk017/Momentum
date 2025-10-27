package com.momentum.app.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.data.DatabaseProvider
import com.momentum.app.model.forms.*
import kotlinx.coroutines.launch
import com.momentum.app.data.store.EmotionStateStore
import com.momentum.app.data.store.ThemePreferences
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
class BienestarViewModel(application: Application) : AndroidViewModel(application) {

    private val _estado = MutableStateFlow(BienestarUiState())
    val estado: StateFlow<BienestarUiState> = _estado

    private val mensajesMotivadores = listOf(
        "Cada día es una nueva oportunidad para crecer",
        "Tu bienestar mental es una prioridad",
        "Pequeños pasos llevan a grandes cambios",
        "Respira profundo, todo va a estar bien",
        "Eres más fuerte de lo que crees",
        "El autocuidado no es egoísmo, es necesario",
        "Hoy es un buen día para sentirte bien",
        "La calma está dentro de ti, solo respira",
        "Cada momento difícil es temporal",
        "Confía en tu proceso de sanación",
        "Mereces amor y compasión, especialmente de ti mismo",
        "Los sentimientos son válidos, permítete sentirlos",
        "Tu salud mental importa más de lo que crees",
        "Está bien pedir ayuda cuando la necesites",
        "Eres capaz de superar cualquier desafío",
        "La mindfulness te ayuda a estar presente",
        "Cada respiración consciente es un acto de amor propio",
        "Progreso, no perfección",
        "Tu vulnerabilidad es tu fortaleza",
        "Mañana es un nuevo comienzo"
    )

    fun seleccionarEstadoEmocional(nombreEstado: String) {
        _estado.update { estadoActual ->
            estadoActual.copy(
                estadoActualSeleccionado = nombreEstado,
                estadosEmocionales = estadoActual.estadosEmocionales.map { estado ->
                    estado.copy(esSeleccionado = estado.nombre == nombreEstado)
                }
            )
        }

        // Persistir última selección para notificaciones
        viewModelScope.launch {
            runCatching { EmotionStateStore(getApplication()).setLastState(nombreEstado) }
                .onFailure { /* log si se desea */ }
        }
    }

    // Simplificar inicialización de usuario
    private val repository by lazy { DatabaseProvider.clientRepository(getApplication()) }
    private val themePrefs by lazy { ThemePreferences(getApplication()) }

    init {
        // Load dark mode preference
        viewModelScope.launch {
            themePrefs.isDarkMode.collect { isDark ->
                _estado.update { it.copy(perfil = it.perfil.copy(modoOscuro = isDark)) }
            }
        }
    }

    fun inicializarConUsuario(email: String) {
        val nombre = email.substringBefore("@")
        _estado.update { it.copy(
            perfil = it.perfil.copy(nombre = nombre, correo = email),
            mensajeMotivadorDelDia = mensajesMotivadores.random()
        )}

        // Intentar cargar desde la base de datos si existe
        viewModelScope.launch {
            runCatching { repository.getByEmail(email) }
                .onSuccess { entity ->
                    if (entity != null) {
                        val partes = entity.name.split(" ")
                        val nombreDb = partes.firstOrNull() ?: entity.name
                        val apellidoDb = partes.drop(1).joinToString(" ")
                        _estado.update { estadoActual ->
                            estadoActual.copy(
                                perfil = estadoActual.perfil.copy(
                                    nombre = nombreDb,
                                    apellido = apellidoDb,
                                    correo = email,
                                    edad = entity.age,
                                    sexo = entity.sex ?: "",
                                    estadoCivil = entity.maritalStatus ?: "",
                                    ocupacion = entity.occupation ?: "",
                                    telefono = entity.phone ?: ""
                                )
                            )
                        }
                    }
                }
        }
    }

    fun inicializarConRegistro(email: String, nombreCompleto: String) {
        val partes = nombreCompleto.split(" ")
        val nombre = partes.firstOrNull() ?: ""
        val apellido = partes.drop(1).joinToString(" ")
        
        _estado.update { it.copy(
            perfil = it.perfil.copy(
                nombre = nombre,
                apellido = apellido,
                correo = email
            ),
            mensajeMotivadorDelDia = mensajesMotivadores.random()
        )}
    }

    fun obtenerNuevoMensajeMotivador() {
        _estado.update { estadoActual ->
            estadoActual.copy(
                mensajeMotivadorDelDia = mensajesMotivadores.random()
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
            
            responderAutomaticamente(mensajeTexto)
        }
    }

    private fun responderAutomaticamente(mensajeUsuario: String) {
        val texto = mensajeUsuario.lowercase()
        val estadoSeleccionado = _estado.value.estadoActualSeleccionado.lowercase()
        
        val respuesta = when {
            // Respuestas para estado ansioso
            texto.contains("ansioso") || estadoSeleccionado.contains("ansioso") -> {
                val frasesAnsiedad = listOf(
                    "🌸 'La ansiedad es temporal, tu fuerza es permanente.' Respira profundo.",
                    "💙 'Cada respiración te devuelve al presente.' Intentemos el ejercicio 4-4-6.",
                    "🦋 'Las mariposas en tu estómago pueden convertirse en alas.' Tu ansiedad puede transformarse.",
                    "🌅 'Este momento difícil pasará, como siempre lo han hecho.' Eres más fuerte de lo que crees."
                )
                frasesAnsiedad.random()
            }
            
            // Respuestas para estado triste
            texto.contains("triste") || estadoSeleccionado.contains("triste") -> {
                val frasesTristeza = listOf(
                    "🌈 'Después de la tormenta siempre sale el sol.' Tu tristeza es válida, pero no es permanente.",
                    "💜 'Las lágrimas limpian el alma.' Permítete sentir, es parte de sanar.",
                    "🌱 'En la oscuridad es donde las semillas germinan.' Tu crecimiento viene en camino.",
                    "⭐ 'Eres luz, incluso cuando no puedes verte brillar.' Tu valor no depende de cómo te sientes hoy."
                )
                frasesTristeza.random()
            }
            
            // Respuestas para estado feliz
            texto.contains("feliz") || texto.contains("bien") || estadoSeleccionado.contains("feliz") -> {
                val frasesFelicidad = listOf(
                    "🎉 '¡Tu sonrisa es contagiosa!' Comparte esa energía positiva con el mundo.",
                    "☀️ 'La felicidad se multiplica cuando se comparte.' ¡Qué hermoso que te sientes así!",
                    "🌟 'Guarda este momento en tu corazón.' Los días felices son regalo para recordar.",
                    "🦋 '¡Celebra cada pequeña victoria!' Tu alegría es medicina para el alma."
                )
                frasesFelicidad.random()
            }
            
            // Respuestas para estado irritado/enojado
            texto.contains("enojado") || texto.contains("irritado") || estadoSeleccionado.contains("irritado") -> {
                val frasesIra = listOf(
                    "🔥 'El enojo es como sostener carbón ardiente.' Suéltalo por tu propia paz.",
                    "🌊 'Deja que la ira sea como una ola: intensa pero que pasa.' Respira y observa.",
                    "🎋 'Sé como el bambú: fuerte pero flexible.' Tu enojo es válido, tu reacción es tu poder.",
                    "💨 'Entre estímulo y respuesta hay un espacio.' Usa ese espacio para elegir tu reacción."
                )
                frasesIra.random()
            }
            
            // Respuestas para estado tranquilo
            texto.contains("tranquilo") || estadoSeleccionado.contains("tranquilo") -> {
                val frasesTranquilidad = listOf(
                    "🕊️ 'La paz que sientes es tu hogar interior.' Disfruta esta serenidad.",
                    "🌸 'En la calma encuentras tu verdadero yo.' Mantén esta energía suave.",
                    "🧘 'La tranquilidad es un superpoder.' Úsala para recargar tu espíritu.",
                    "🌙 'Tu alma está en armonía.' Este es el estado natural de tu ser."
                )
                frasesTranquilidad.random()
            }
            
            // Respuestas para estado con energía
            texto.contains("energía") || estadoSeleccionado.contains("energía") -> {
                val frasesEnergia = listOf(
                    "⚡ '¡Tu energía puede mover montañas!' Canaliza esa fuerza hacia tus sueños.",
                    "🚀 'Tienes el combustible para alcanzar las estrellas.' ¡Qué increíble vitalidad!",
                    "🌟 'Tu entusiasmo ilumina todo a tu alrededor.' Comparte esa chispa con otros.",
                    "🔋 'Estás completamente cargado de posibilidades.' ¡El mundo necesita tu energía!"
                )
                frasesEnergia.random()
            }
            
            // Consejos motivadores generales
            texto.contains("consejo") || texto.contains("ayuda") -> {
                val consejosMotivadores = listOf(
                    "💡 Consejo del día: 'Cada mañana tienes dos opciones: continuar durmiendo con tus sueños, o levantarte y perseguirlos.'",
                    "🌱 Recuerda: 'El progreso, no la perfección.' Cada pequeño paso cuenta hacia tu bienestar.",
                    "🎯 Enfócate en lo que SÍ puedes controlar: tu respiración, tus pensamientos, tus acciones.",
                    "🧠 Práctica la regla 3-3-3: Nombra 3 cosas que ves, 3 sonidos que escuchas, 3 partes de tu cuerpo que sientes."
                )
                consejosMotivadores.random()
            }
            
            // Respuestas para agradecimientos
            texto.contains("gracias") -> {
                val respuestasGracias = listOf(
                    "💝 'De nada. Cuidar tu bienestar es lo más importante.' Estoy aquí siempre que me necesites.",
                    "🤗 'Es un honor acompañarte en este viaje.' Tu bienestar mental es una prioridad.",
                    "✨ 'Gracias a ti por cuidarte.' Eres valiente por buscar apoyo.",
                    "🌈 'Juntos hacemos un gran equipo.' Sigamos construyendo tu bienestar."
                )
                respuestasGracias.random()
            }
            
            // Respuestas motivadoras por defecto
            else -> {
                val motivacionGeneral = listOf(
                    "🌟 'Eres más valiente de lo que crees, más fuerte de lo que pareces.' Cuéntame más sobre ti.",
                    "💪 'Cada día que eliges cuidar tu bienestar es una victoria.' ¿Qué sientes ahora?",
                    "🦋 'Tu historia aún se está escribiendo.' ¿Qué capítulo quieres crear hoy?",
                    "🌱 'El crecimiento personal es un viaje, no un destino.' ¿En qué puedo acompañarte?",
                    "💎 'Eres una obra maestra en progreso.' Cuéntame qué está pasando en tu mundo."
                )
                motivacionGeneral.random()
            }
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
        
        // Enviar consejo adicional después de 3 segundos
        enviarConsejoAdicional(texto, estadoSeleccionado)
    }
    
    private fun enviarConsejoAdicional(texto: String, estadoSeleccionado: String) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            
            val consejoExtra = when {
                texto.contains("ansioso") || estadoSeleccionado.contains("ansioso") -> 
                    "💡 Técnica anti-ansiedad: Cuenta 5 cosas que ves, 4 que tocas, 3 que escuchas, 2 que hueles, 1 que saboreas."
                
                texto.contains("triste") || estadoSeleccionado.contains("triste") -> 
                    "🌈 Consejo: Escribe 3 cosas buenas que te pasaron hoy, por pequeñas que sean. La gratitud sana el corazón."
                
                texto.contains("irritado") || estadoSeleccionado.contains("irritado") -> 
                    "🌊 Técnica de calma: Inhala por 4, mantén por 4, exhala por 6. Repite 3 veces para encontrar tu centro."
                
                texto.contains("feliz") -> 
                    "✨ Consejo de felicidad: Comparte tu alegría con alguien hoy. La felicidad compartida se multiplica."
                
                else -> null
            }
            
            consejoExtra?.let { consejo ->
                val mensajeConsejo = MensajeChat(
                    id = UUID.randomUUID().toString(),
                    contenido = consejo,
                    esDelUsuario = false
                )
                
                _estado.update { estadoActual ->
                    estadoActual.copy(
                        mensajesChat = estadoActual.mensajesChat + mensajeConsejo
                    )
                }
            }
        }
    }

    fun onEntradaDiarioChange(entrada: String) {
        _estado.update { it.copy(entradaDiarioNueva = entrada) }
    }

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
                android.util.Log.e("BienestarViewModel", "Error al guardar entrada", e)
            }
        }
    }

    fun eliminarEntradaDiario(entrada: EntradaDiario) {
        _estado.update { estadoActual ->
            estadoActual.copy(
                entradasDiario = estadoActual.entradasDiario.filter { it.id != entrada.id }
            )
        }
    }

    // Simplificar ejercicio de respiración
    fun iniciarEjercicioRespiracion() {
        _estado.update { it.copy(ejercicioRespiracionActivo = true) }
    }

    fun detenerEjercicioRespiracion() {
        _estado.update { it.copy(
            ejercicioRespiracionActivo = false,
            estadisticas = it.estadisticas.copy(
                sesionesRespiracion = it.estadisticas.sesionesRespiracion + 1
            )
        )}
    }

    fun onActualizarEstadoEmocional(estadoEmocional: String) {
        _estado.update { estadoActual ->
            estadoActual.copy(
                estadoActualSeleccionado = estadoEmocional,
                estadosEmocionales = estadoActual.estadosEmocionales.map { estado ->
                    estado.copy(esSeleccionado = estado.nombre == estadoEmocional)
                }
            )
        }
    }

    fun cerrarSesion(onLogout: () -> Unit) {
        _estado.value = BienestarUiState()
        onLogout()
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

        // Persistir cambios en BD (nombre completo: nombre + apellido)
        val nombreCompleto = listOf(nombre, apellido).filter { it.isNotBlank() }.joinToString(" ")
        viewModelScope.launch {
            val perfil = _estado.value.perfil
            runCatching {
                repository.upsert(
                    name = nombreCompleto.ifBlank { nombre },
                    email = correo,
                    age = perfil.edad,
                    sex = perfil.sexo.ifBlank { null },
                    maritalStatus = perfil.estadoCivil.ifBlank { null },
                    occupation = perfil.ocupacion.ifBlank { null },
                    phone = perfil.telefono.ifBlank { null }
                )
            }
                .onFailure { /* log si se desea */ }
        }
    }

    fun actualizarPerfilCliente(form: ClientProfileForm) {
        // Actualiza el estado en memoria con los nuevos campos del cliente
        _estado.update { estadoActual ->
            estadoActual.copy(
                perfil = estadoActual.perfil.copy(
                    nombre = form.nombre.ifBlank { estadoActual.perfil.nombre },
                    // mantenemos apellido existente; nombre en form se asume nombre(s) sin apellido
                    correo = form.email.ifBlank { estadoActual.perfil.correo },
                    edad = form.edad,
                    sexo = form.sexo,
                    estadoCivil = form.estadoCivil,
                    ocupacion = form.ocupacion,
                    telefono = form.telefono
                )
            )
        }

        // Persistimos al menos nombre completo y correo como antes
        val nombreCompleto = listOf(_estado.value.perfil.nombre, _estado.value.perfil.apellido)
            .filter { it.isNotBlank() }
            .joinToString(" ")
        viewModelScope.launch {
            val perfil = _estado.value.perfil
            runCatching {
                repository.upsert(
                    name = nombreCompleto.ifBlank { perfil.nombre },
                    email = perfil.correo,
                    age = perfil.edad,
                    sex = perfil.sexo.ifBlank { null },
                    maritalStatus = perfil.estadoCivil.ifBlank { null },
                    occupation = perfil.ocupacion.ifBlank { null },
                    phone = perfil.telefono.ifBlank { null }
                )
            }
                .onFailure { /* log si se desea */ }
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

    fun toggleModoOscuro() {
        val nuevoValor = !_estado.value.perfil.modoOscuro
        _estado.update { estadoActual ->
            estadoActual.copy(
                perfil = estadoActual.perfil.copy(
                    modoOscuro = nuevoValor
                )
            )
        }
        viewModelScope.launch {
            themePrefs.setDarkMode(nuevoValor)
        }
    }
}