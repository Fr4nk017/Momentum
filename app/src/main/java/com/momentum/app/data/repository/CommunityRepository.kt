package com.momentum.app.data.repository

import com.momentum.app.data.local.CommunityPostDao
import com.momentum.app.data.local.CommunityPostEntity
import kotlinx.coroutines.flow.Flow

class CommunityRepository(private val dao: CommunityPostDao) {
    suspend fun insert(post: CommunityPostEntity): Long = dao.insert(post)
    
    fun observeAll(): Flow<List<CommunityPostEntity>> = dao.observeAll()
    
    fun observeByUser(userId: Long): Flow<List<CommunityPostEntity>> = 
        dao.observeByUser(userId)
    
    suspend fun incrementLikes(postId: Long) = dao.incrementLikes(postId)
    
    suspend fun incrementComments(postId: Long) = dao.incrementComments(postId)
}