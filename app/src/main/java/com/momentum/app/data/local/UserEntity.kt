package com.momentum.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val bio: String = "",
    val avatarUrl: String = "",
    val totalPosts: Int = 0,
    val friendsCount: Int = 0,
    val currentMood: String = "",
    val lastDiaryDate: Long = 0,
    val totalEntries: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)