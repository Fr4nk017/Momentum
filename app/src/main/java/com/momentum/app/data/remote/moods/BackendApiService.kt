package com.momentum.app.data.remote.moods

import com.momentum.app.data.remote.diary.DiaryEntryRequest
import com.momentum.app.data.remote.diary.DiaryEntryResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BackendApiService {

    // Moods endpoints
    @POST("api/moods")
    suspend fun createMood(
        @Body request: MoodEntryRequest
    ): MoodEntryResponse

    @GET("api/moods")
    suspend fun getMoods(
        @Query("userId") userId: String
    ): List<MoodEntryResponse>

    // Diary endpoints
    @POST("api/diary")
    suspend fun createDiaryEntry(
        @Body request: DiaryEntryRequest
    ): DiaryEntryResponse

    @GET("api/diary")
    suspend fun getDiaryEntries(
        @Query("userId") userId: String
    ): List<DiaryEntryResponse>
}
