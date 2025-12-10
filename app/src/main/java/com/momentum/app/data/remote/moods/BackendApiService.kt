package com.momentum.app.data.remote.moods

import com.momentum.app.data.remote.diary.DiaryEntryRequest
import com.momentum.app.data.remote.diary.DiaryEntryResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @PUT("api/moods/{id}")
    suspend fun updateMood(
        @Path("id") id: String,
        @Body request: MoodEntryRequest
    ): MoodEntryResponse

    @DELETE("api/moods/{id}")
    suspend fun deleteMood(
        @Path("id") id: String
    )

    // Diary endpoints
    @POST("api/diary")
    suspend fun createDiaryEntry(
        @Body request: DiaryEntryRequest
    ): DiaryEntryResponse

    @GET("api/diary")
    suspend fun getDiaryEntries(
        @Query("userId") userId: String
    ): List<DiaryEntryResponse>

    @PUT("api/diary/{id}")
    suspend fun updateDiaryEntry(
        @Path("id") id: String,
        @Body request: DiaryEntryRequest
    ): DiaryEntryResponse

    @DELETE("api/diary/{id}")
    suspend fun deleteDiaryEntry(
        @Path("id") id: String
    )
}
