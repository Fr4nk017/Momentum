package com.momentum.app.data.suggestions

data class Suggestion(
    val badge: String,
    val title: String,
    val description: String,
    val actionLabel: String,
    val actionType: ActionType
)

enum class ActionType { BREATHING_446, OPEN_DIARY, OPEN_CHAT, MINDFULNESS }

object SuggestionManager {
    fun forMood(mood: String, intensity: Int): Suggestion {
        // intensity: 1=leve, 2=moderado, 3=intenso
        return when (mood) {
            "😊", "😌", "❤️", "🏆" -> Suggestion(
                badge = "💡 Recomendación personalizada",
                title = "Respiración 4-4-6 - 5 repeticiones",
                description = "Perfecto para tu estado de ánimo actual",
                actionLabel = "Comenzar ahora",
                actionType = ActionType.BREATHING_446
            )
            "😰", "😢", "😠" -> Suggestion(
                badge = "💡 Recomendación personalizada",
                title = "Respiración 4-4-6 - 5 repeticiones",
                description = if (intensity >= 2) "Te ayudará a regular emociones intensas" else "Respira y recupéra tu calma",
                actionLabel = "Comenzar ahora",
                actionType = ActionType.BREATHING_446
            )
            "😴" -> Suggestion(
                badge = "💡 Recomendación personalizada",
                title = "Mindfulness 1 minuto",
                description = "Una pausa breve para reconectar",
                actionLabel = "Iniciar",
                actionType = ActionType.MINDFULNESS
            )
            else -> Suggestion(
                badge = "💡 Recomendación personalizada",
                title = "Escribe un check-in en tu diario",
                description = "Ponerlo en palabras te ayudará a procesarlo",
                actionLabel = "Abrir diario",
                actionType = ActionType.OPEN_DIARY
            )
        }
    }

    val jokes = listOf(
        "Hoy tu cerebro hizo 10,000 pensamientos. ¡Y 9,999 fueron geniales!",
        "Recordatorio: respirar cuenta como productividad.",
        "Tu yo del futuro ya te está aplaudiendo por abrir la app hoy.",
        "¿Sabías que sonreír quema calorías? ¡Vamos por ese cardio emocional!"
    )
}