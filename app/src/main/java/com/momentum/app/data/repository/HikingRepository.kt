package com.momentum.app.data.repository

import com.momentum.app.data.local.HikingSessionDao
import com.momentum.app.data.local.HikingSessionEntity
import com.momentum.app.data.local.LocationPointDao
import com.momentum.app.data.local.LocationPointEntity
import kotlinx.coroutines.flow.Flow

class HikingRepository(
    private val sessionDao: HikingSessionDao,
    private val locationDao: LocationPointDao
) {
    // Session operations
    suspend fun createSession(session: HikingSessionEntity): Long {
        return sessionDao.insert(session)
    }

    suspend fun updateSession(session: HikingSessionEntity) {
        sessionDao.update(session)
    }

    suspend fun getSessionById(sessionId: Long): HikingSessionEntity? {
        return sessionDao.getSessionById(sessionId)
    }

    fun getCompletedSessions(): Flow<List<HikingSessionEntity>> {
        return sessionDao.getCompletedSessions()
    }

    suspend fun getActiveSession(): HikingSessionEntity? {
        return sessionDao.getActiveSession()
    }

    suspend fun deleteSession(sessionId: Long) {
        sessionDao.deleteSession(sessionId)
    }

    // Location point operations
    suspend fun addLocationPoint(point: LocationPointEntity): Long {
        return locationDao.insert(point)
    }

    suspend fun addLocationPoints(points: List<LocationPointEntity>) {
        locationDao.insertAll(points)
    }

    suspend fun getLocationPointsForSession(sessionId: Long): List<LocationPointEntity> {
        return locationDao.getPointsForSession(sessionId)
    }

    fun observeLocationPointsForSession(sessionId: Long): Flow<List<LocationPointEntity>> {
        return locationDao.observePointsForSession(sessionId)
    }

    suspend fun deleteLocationPointsForSession(sessionId: Long) {
        locationDao.deletePointsForSession(sessionId)
    }
}
