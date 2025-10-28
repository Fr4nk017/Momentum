package com.momentum.app.data

import android.content.Context
import androidx.room.Room
import com.momentum.app.data.local.AppDatabase
import com.momentum.app.data.repository.ClientRepository
import com.momentum.app.data.repository.UserRepository
import com.momentum.app.data.repository.FriendRepository
import com.momentum.app.data.repository.CommunityRepository
import com.momentum.app.data.repository.RecentPlaceRepository

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        // Double-checked locking pattern
        return INSTANCE ?: synchronized(this) {
            // Check again inside synchronized block
            val instance = INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "momentum.db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            instance
        }
    }

    fun clientRepository(context: Context): ClientRepository {
        return ClientRepository(getDatabase(context).clientDao())
    }

    fun userRepository(context: Context): UserRepository {
        return UserRepository(getDatabase(context).userDao())
    }

    fun friendRepository(context: Context): FriendRepository {
        return FriendRepository(getDatabase(context).friendDao())
    }

    fun communityRepository(context: Context): CommunityRepository {
        return CommunityRepository(getDatabase(context).communityPostDao())
    }

    fun recentPlaceRepository(context: Context): RecentPlaceRepository {
        return RecentPlaceRepository(getDatabase(context).recentPlaceDao())
    }
}