package com.momentum.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_places")
data class RecentPlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // "park", "trail", "psychologist", "support_center"
    val searchedAt: Long = System.currentTimeMillis()
)
