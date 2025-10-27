package com.momentum.app.data.repository

import com.momentum.app.data.local.ClientDao
import com.momentum.app.data.local.ClientEntity
import kotlinx.coroutines.flow.Flow

class ClientRepository(private val dao: ClientDao) {
    suspend fun insert(name: String, email: String): Long {
        return dao.insert(ClientEntity(name = name, email = email))
    }

    suspend fun getByEmail(email: String): ClientEntity? = dao.findByEmail(email)

    fun observeAll(): Flow<List<ClientEntity>> = dao.observeAll()

    suspend fun upsert(name: String, email: String) {
        dao.upsert(ClientEntity(name = name, email = email))
    }
}