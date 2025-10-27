package com.momentum.app.data

import android.content.Context
import androidx.room.Room
import com.momentum.app.data.local.AppDatabase
import com.momentum.app.data.local.ClientDao
import com.momentum.app.data.repository.ClientRepository

object DatabaseProvider {
    @Volatile
    private var db: AppDatabase? = null

    private fun getDatabase(context: Context): AppDatabase {
        return db ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "momentum.db"
            ).build()
            db = instance
            instance
        }
    }

    fun clientRepository(context: Context): ClientRepository {
        return ClientRepository(getDatabase(context).clientDao())
    }
}