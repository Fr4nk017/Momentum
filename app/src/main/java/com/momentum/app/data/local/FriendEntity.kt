package com.momentum.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val friendUserId: Long,
    val status: String, // "pending", "accepted", "blocked"
    val createdAt: Long = System.currentTimeMillis()
)