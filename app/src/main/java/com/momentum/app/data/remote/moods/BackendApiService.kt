package com.momentum.app.data.remote.moods

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BackendApiService {

    @POST("api/moods")
    suspend fun createMood(
        @Body request: MoodEntryRequest
    ): MoodEntryResponse

    @GET("api/moods")
    suspend fun getMoods(
        @Query("userId") userId: String
    ): List<MoodEntryResponse>
}
