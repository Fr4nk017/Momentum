package com.momentum.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentPlaceDao {
    @Insert
    suspend fun insert(place: RecentPlaceEntity)

    @Query("SELECT * FROM recent_places ORDER BY searchedAt DESC LIMIT 10")
    fun getRecent(): Flow<List<RecentPlaceEntity>>

    @Query("DELETE FROM recent_places WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM recent_places")
    suspend fun clearAll()
}
