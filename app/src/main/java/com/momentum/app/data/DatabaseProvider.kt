package com.momentum.app.data

import android.content.Context
import androidx.room.Room
import com.momentum.app.data.local.AppDatabase
import com.momentum.app.data.repository.ClientRepository
import com.momentum.app.data.repository.UserRepository
import com.momentum.app.data.repository.FriendRepository
import com.momentum.app.data.repository.CommunityRepository

object DatabaseProvider {
    @Volatile
    private var db: AppDatabase? = null

    private fun getDatabase(context: Context): AppDatabase {
        return db ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "momentum.db"
            )
                .fallbackToDestructiveMigration()
                .build()
            db = instance
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
}