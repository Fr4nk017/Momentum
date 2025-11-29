package com.momentum.app.data.remote.diary

data class DiaryEntryResponse(
    val id: String?,
    val userId: String,
    val title: String,
    val content: String,
    val date: String
)
