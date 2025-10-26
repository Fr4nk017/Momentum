package com.momentum.app

import android.app.Application
import androidx.room.Room
import com.momentum.app.data.AppDatabase

class App : Application() {
    lateinit var db: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "momentum-db"
        ).build()
    }
}
