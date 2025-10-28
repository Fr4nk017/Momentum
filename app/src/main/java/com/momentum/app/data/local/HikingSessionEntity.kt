package com.momentum.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hiking_sessions")
data class HikingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val totalDistance: Float = 0f, // en metros
    val totalSteps: Int = 0,
    val totalCalories: Float = 0f,
    val averageSpeed: Float = 0f, // m/s
    val maxSpeed: Float = 0f,
    val duration: Long = 0, // en milisegundos
    val isCompleted: Boolean = false
)
