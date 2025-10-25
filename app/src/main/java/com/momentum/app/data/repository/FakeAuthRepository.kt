package com.momentum.app.data.repository

import com.momentum.app.domain.repository.AuthRepository
import com.momentum.app.domain.repository.AuthResult
import javax.inject.Inject
import kotlin.random.Random

class FakeAuthRepository @Inject constructor() : AuthRepository {

    companion object {
        private const val FAKE_AUTH_DELAY = 1500L // Simulated network delay
        private val VALID_CREDENTIALS = mapOf(
            "test@momentum.com" to "password123",
            "admin@momentum.com" to "admin123"
        )
    }

    override suspend fun login(email: String, password: String): Result<AuthResult> {
        // Simulate network delay
        kotlinx.coroutines.delay(FAKE_AUTH_DELAY)
        
        return when {
            email.isBlank() || password.isBlank() -> {
                Result.failure(IllegalArgumentException("Email and password cannot be empty"))
            }
            VALID_CREDENTIALS[email] == password -> {
                // Generate a fake JWT token
                val token = generateFakeToken()
                Result.success(AuthResult(token = token, email = email))
            }
            else -> {
                Result.failure(IllegalArgumentException("Invalid credentials"))
            }
        }
    }

    override suspend fun logout() {
        // Simulate network delay
        kotlinx.coroutines.delay(FAKE_AUTH_DELAY)
    }

    private fun generateFakeToken(): String {
        val alphabet = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..32)
            .map { alphabet[Random.nextInt(0, alphabet.size)] }
            .joinToString("")
    }
}