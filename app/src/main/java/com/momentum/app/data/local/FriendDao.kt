package com.momentum.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Insert
    suspend fun insert(friend: FriendEntity): Long

    @Query("SELECT * FROM friends WHERE userId = :userId AND status = 'accepted'")
    fun observeAcceptedFriends(userId: Long): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE userId = :userId AND status = 'pending'")
    fun observePendingRequests(userId: Long): Flow<List<FriendEntity>>

    @Query("UPDATE friends SET status = :status WHERE id = :friendId")
    suspend fun updateStatus(friendId: Long, status: String)

    @Query("SELECT COUNT(*) FROM friends WHERE userId = :userId AND status = 'accepted'")
    suspend fun getFriendsCount(userId: Long): Int
}