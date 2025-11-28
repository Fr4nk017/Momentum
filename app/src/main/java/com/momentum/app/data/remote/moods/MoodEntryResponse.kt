package com.momentum.app.data.remote.moods

data class MoodEntryResponse(
    val id: String?,
    val userId: String,
    val emotion: String,
    val note: String?,
    val date: String
)
