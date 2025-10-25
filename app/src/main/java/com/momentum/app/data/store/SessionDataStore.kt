package com.momentum.app.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionDataStore @Inject constructor(private val context: Context) {

    private object PreferencesKeys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    /**
     * Saves user session data to DataStore
     * @param authToken JWT authentication token
     * @param userEmail User's email address
     */
    suspend fun saveSession(authToken: String, userEmail: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN] = authToken
            preferences[PreferencesKeys.USER_EMAIL] = userEmail
        }
    }

    /**
     * Clears all session data from DataStore
     */
    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Flow that emits the current authentication token
     */
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTH_TOKEN]
    }

    /**
     * Flow that emits the current user's email
     */
    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_EMAIL]
    }

    /**
     * Flow that emits true if the user is currently logged in (has a non-null, non-empty auth token)
     */
    val isLoggedIn: Flow<Boolean> = authToken.map { token ->
        !token.isNullOrEmpty()
    }
}