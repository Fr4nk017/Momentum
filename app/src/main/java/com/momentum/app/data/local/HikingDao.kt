package com.momentum.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HikingSessionDao {
    @Insert
    suspend fun insert(session: HikingSessionEntity): Long

    @Update
    suspend fun update(session: HikingSessionEntity)

    @Query("SELECT * FROM hiking_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): HikingSessionEntity?

    @Query("SELECT * FROM hiking_sessions WHERE isCompleted = 1 ORDER BY startTime DESC")
    fun getCompletedSessions(): Flow<List<HikingSessionEntity>>

    @Query("SELECT * FROM hiking_sessions WHERE isCompleted = 0 LIMIT 1")
    suspend fun getActiveSession(): HikingSessionEntity?

    @Query("DELETE FROM hiking_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)
}

@Dao
interface LocationPointDao {
    @Insert
    suspend fun insert(point: LocationPointEntity): Long

    @Insert
    suspend fun insertAll(points: List<LocationPointEntity>)

    @Query("SELECT * FROM location_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getPointsForSession(sessionId: Long): List<LocationPointEntity>

    @Query("SELECT * FROM location_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun observePointsForSession(sessionId: Long): Flow<List<LocationPointEntity>>

    @Query("DELETE FROM location_points WHERE sessionId = :sessionId")
    suspend fun deletePointsForSession(sessionId: Long)
}
