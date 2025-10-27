package com.momentum.app.data.repository

import com.momentum.app.data.local.UserDao
import com.momentum.app.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao: UserDao) {
    suspend fun insert(user: UserEntity): Long = dao.insert(user)
    
    suspend fun update(user: UserEntity) = dao.update(user)
    
    suspend fun findByEmail(email: String): UserEntity? = dao.findByEmail(email)
    
    suspend fun findById(userId: Long): UserEntity? = dao.findById(userId)
    
    fun observeAll(): Flow<List<UserEntity>> = dao.observeAll()
    
    suspend fun searchUsers(query: String): List<UserEntity> = dao.searchUsers(query)
}