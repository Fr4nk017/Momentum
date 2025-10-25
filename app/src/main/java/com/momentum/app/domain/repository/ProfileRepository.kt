package com.momentum.app.domain.repository

import com.momentum.app.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun saveProfile(profile: Profile): Result<Unit>
    fun getProfile(): Flow<Profile?>
}