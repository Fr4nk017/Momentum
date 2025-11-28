package com.momentum.app.data.remote.moods

data class MoodEntryRequest(
    val userId: String,
    val emotion: String,
    val note: String?,
    val date: String? = null // "YYYY-MM-DD" (opcional, el backend puede usar LocalDate.now())
)
