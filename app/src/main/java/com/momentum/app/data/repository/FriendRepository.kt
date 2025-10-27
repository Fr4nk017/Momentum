package com.momentum.app.data.repository

import com.momentum.app.data.local.FriendDao
import com.momentum.app.data.local.FriendEntity
import kotlinx.coroutines.flow.Flow

class FriendRepository(private val dao: FriendDao) {
    suspend fun insert(friend: FriendEntity): Long = dao.insert(friend)
    
    fun observeAcceptedFriends(userId: Long): Flow<List<FriendEntity>> = 
        dao.observeAcceptedFriends(userId)
    
    fun observePendingRequests(userId: Long): Flow<List<FriendEntity>> = 
        dao.observePendingRequests(userId)
    
    suspend fun updateStatus(friendId: Long, status: String) = 
        dao.updateStatus(friendId, status)
    
    suspend fun getFriendsCount(userId: Long): Int = 
        dao.getFriendsCount(userId)
}