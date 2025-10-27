package com.momentum.app.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.emotionDataStore by preferencesDataStore(name = "emotion_state")

class EmotionStateStore(private val context: Context) {
    private val KEY_LAST_STATE = stringPreferencesKey("last_state")
    private val KEY_LAST_TIME = longPreferencesKey("last_time")

    suspend fun setLastState(state: String, timeMillis: Long = System.currentTimeMillis()) {
        context.emotionDataStore.edit { prefs ->
            prefs[KEY_LAST_STATE] = state
            prefs[KEY_LAST_TIME] = timeMillis
        }
    }

    fun lastStateFlow(): Flow<Pair<String?, Long?>> {
        return context.emotionDataStore.data.map { prefs ->
            val s = prefs[KEY_LAST_STATE]
            val t = prefs[KEY_LAST_TIME]
            s to t
        }
    }

    suspend fun getLastState(): Pair<String?, Long?> {
        return lastStateFlow().first()
    }
}