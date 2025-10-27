package com.momentum.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(client: ClientEntity): Long

    @Query("SELECT * FROM clients WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): ClientEntity?

    @Query("SELECT * FROM clients ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ClientEntity>>

    @Upsert
    suspend fun upsert(client: ClientEntity)
}