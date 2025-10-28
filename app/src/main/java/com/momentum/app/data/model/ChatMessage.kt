package com.momentum.app.data.model

data class ChatMessage(
    val id: Long = 0,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: MessageType = MessageType.TEXT,
    val metadata: MessageMetadata? = null
)

enum class MessageType {
    TEXT,
    BREATHING_EXERCISE,
    MEDITATION,
    GROUNDING_TECHNIQUE,
    REFLECTION_PROMPT,
    DIARY_SUGGESTION,
    QUICK_TIP
}

data class MessageMetadata(
    val exerciseType: String? = null,
    val duration: Int? = null, // en segundos
    val moodContext: String? = null,
    val relatedDiaryEntryId: Long? = null
)

data class ChatSession(
    val id: Long = 0,
    val userId: String,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val userMood: String? = null,
    val topicTags: List<String> = emptyList(),
    val messageCount: Int = 0
)

// Respuestas predefinidas según contexto
object ChatResponses {
    val greetings = listOf(
        "Hola, estoy aquí para apoyarte. ¿Cómo te sientes hoy?",
        "Bienvenido/a. ¿Qué te gustaría compartir conmigo?",
        "Hola, es un gusto verte. ¿Cómo ha estado tu día?"
    )
    
    val anxietyResponses = listOf(
        "Entiendo que sientes ansiedad. Vamos a hacer un ejercicio de respiración juntos. ¿Estás listo/a?",
        "La ansiedad puede ser abrumadora. Te sugiero que probemos una técnica de grounding. Te ayudará a conectar con el presente.",
        "Siento que estés pasando por esto. ¿Te gustaría escribir sobre lo que sientes en tu diario?"
    )
    
    val sadnessResponses = listOf(
        "Es completamente válido sentirse triste. Tus emociones son importantes. ¿Quieres hablar sobre lo que te está afectando?",
        "La tristeza es parte de ser humano. ¿Te ayudaría recordar momentos en los que te sentiste mejor?",
        "Estoy aquí para ti. A veces escribir en el diario puede ayudar a procesar estos sentimientos. ¿Lo intentamos?"
    )
    
    val reflectionPrompts = listOf(
        "¿Qué es una cosa pequeña por la que te sientes agradecido/a hoy?",
        "¿Cuál fue un momento de tu día en el que te sentiste bien?",
        "¿Qué necesitas en este momento para sentirte un poco mejor?",
        "¿Hay algo que hayas logrado hoy, por pequeño que sea?"
    )
    
    val encouragement = listOf(
        "Has dado un gran paso al expresar cómo te sientes. Eso requiere valentía.",
        "Recuerda que está bien no estar bien todo el tiempo. Eres más fuerte de lo que crees.",
        "Cada día que intentas cuidar tu salud mental es una victoria. Estoy orgulloso/a de ti.",
        "Tus sentimientos son válidos. Gracias por confiar en mí."
    )
    
    val breathingExercise = """
        Vamos a hacer un ejercicio de respiración 4-4-6:
        
        1. Inhala profundamente por 4 segundos
        2. Mantén el aire por 4 segundos
        3. Exhala lentamente por 6 segundos
        
        Repetiremos esto 5 veces. ¿Comenzamos?
    """.trimIndent()
    
    val groundingTechnique = """
        Técnica de Grounding 5-4-3-2-1:
        
        Identifica a tu alrededor:
        • 5 cosas que puedes VER
        • 4 cosas que puedes TOCAR
        • 3 cosas que puedes ESCUCHAR
        • 2 cosas que puedes OLER
        • 1 cosa que puedes SABOREAR
        
        Esta técnica te ayuda a conectar con el presente.
    """.trimIndent()
    
    val mindfulnessTip = """
        Ejercicio de Mindfulness (1 minuto):
        
        1. Cierra los ojos o enfoca tu mirada en un punto
        2. Presta atención a tu respiración natural
        3. Si tu mente divaga, suavemente regresa tu atención a la respiración
        4. No juzgues tus pensamientos, solo obsérvalos pasar
        
        Un minuto puede hacer una gran diferencia.
    """.trimIndent()
}

// Keywords para detección de contexto
object EmotionalKeywords {
    val anxiety = listOf(
        "ansiedad", "ansioso", "ansiosa", "nervioso", "nerviosa", 
        "preocupado", "preocupada", "estresado", "estresada",
        "pánico", "angustia", "miedo", "terror"
    )
    
    val sadness = listOf(
        "triste", "tristeza", "deprimido", "deprimida", "solo", "sola",
        "vacío", "vacía", "desesperanzado", "desesperanzada",
        "melancolía", "dolor", "llorar", "llanto"
    )
    
    val anger = listOf(
        "enojado", "enojada", "enojo", "ira", "rabia", "furioso", "furiosa",
        "molesto", "molesta", "frustrado", "frustrada", "irritado", "irritada"
    )
    
    val overwhelmed = listOf(
        "abrumado", "abrumada", "sobrepasado", "sobrepasada",
        "agobiado", "agobiada", "no puedo", "es demasiado"
    )
    
    val positive = listOf(
        "feliz", "contento", "contenta", "alegre", "bien", "mejor",
        "agradecido", "agradecida", "esperanzado", "esperanzada"
    )
}
