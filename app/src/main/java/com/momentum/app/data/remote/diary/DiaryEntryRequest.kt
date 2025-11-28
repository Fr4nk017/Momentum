package com.momentum.app.data.remote.diary

data class DiaryEntryRequest(
    val userId: String,
    val title: String,
    val content: String,
    val date: String? = null // "YYYY-MM-DD" (opcional, el backend puede usar LocalDate.now())
)
