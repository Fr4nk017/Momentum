package com.momentum.app.data.repository

import com.momentum.app.data.store.SessionDataStore
import com.momentum.app.domain.model.Profile
import com.momentum.app.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeProfileRepository @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : ProfileRepository {
    
    private val profileData = MutableStateFlow<Profile?>(null)

    override suspend fun saveProfile(profile: Profile): Result<Unit> {
        return try {
            kotlinx.coroutines.delay(1000) // Simular latencia de red
            profileData.value = profile
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getProfile(): Flow<Profile?> {
        return combine(
            profileData,
            sessionDataStore.userEmail
        ) { profile, email ->
            profile?.copy(email = email ?: "") ?: email?.let { 
                Profile(
                    name = "",
                    email = it,
                    phone = null,
                    avatarUrl = null
                )
            }
        }
    }
}