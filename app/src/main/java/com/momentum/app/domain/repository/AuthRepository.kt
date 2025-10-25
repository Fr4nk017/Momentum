package com.momentum.app.domain.repository

interface AuthRepository {
    /**
     * Attempts to login a user with the given credentials
     * @param email User's email
     * @param password User's password
     * @return Result containing the auth token if successful, or an exception if failed
     */
    suspend fun login(email: String, password: String): Result<AuthResult>

    /**
     * Logs out the current user
     */
    suspend fun logout()
}

data class AuthResult(
    val token: String,
    val email: String
)