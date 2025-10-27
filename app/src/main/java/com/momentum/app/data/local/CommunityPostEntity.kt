package com.momentum.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val userName: String,
    val content: String,
    val emotionalState: String = "",
    val likes: Int = 0,
    val comments: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)