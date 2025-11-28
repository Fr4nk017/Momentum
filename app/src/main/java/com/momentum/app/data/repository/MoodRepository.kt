package com.momentum.app.data.repository

import com.momentum.app.data.remote.moods.BackendApiService
import com.momentum.app.data.remote.moods.MoodEntryRequest
import com.momentum.app.data.remote.moods.MoodEntryResponse

class MoodRepository(
    private val api: BackendApiService
) {

    suspend fun enviarMood(
        userId: String,
        emotion: String,
        note: String?
    ): MoodEntryResponse {
        val request = MoodEntryRequest(
            userId = userId,
            emotion = emotion,
            note = note,
            date = null // el backend usará LocalDate.now()
        )
        return api.createMood(request)
    }

    suspend fun obtenerHistorial(userId: String): List<MoodEntryResponse> {
        return api.getMoods(userId)
    }
}
