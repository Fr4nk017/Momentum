package com.momentum.app.data.repository

import com.momentum.app.data.remote.moods.BackendApiService
import com.momentum.app.data.remote.diary.DiaryEntryRequest
import com.momentum.app.data.remote.diary.DiaryEntryResponse

class RemoteDiaryRepository(
    private val api: BackendApiService
) {

    suspend fun enviarEntrada(
        userId: String,
        title: String,
        content: String
    ): DiaryEntryResponse {
        val request = DiaryEntryRequest(
            userId = userId,
            title = title,
            content = content,
            date = null // el backend usará LocalDate.now()
        )
        return api.createDiaryEntry(request)
    }

    suspend fun obtenerEntradas(userId: String): List<DiaryEntryResponse> {
        return api.getDiaryEntries(userId)
    }
}
