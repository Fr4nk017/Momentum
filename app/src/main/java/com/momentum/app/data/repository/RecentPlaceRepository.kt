package com.momentum.app.data.repository

import com.momentum.app.data.local.RecentPlaceDao
import com.momentum.app.data.local.RecentPlaceEntity
import kotlinx.coroutines.flow.Flow

class RecentPlaceRepository(private val dao: RecentPlaceDao) {
    suspend fun addRecentPlace(name: String, category: String) {
        dao.insert(RecentPlaceEntity(name = name, category = category))
    }

    fun getRecentPlaces(): Flow<List<RecentPlaceEntity>> = dao.getRecent()

    suspend fun delete(id: Long) = dao.delete(id)

    suspend fun clearAll() = dao.clearAll()
}
