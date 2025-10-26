package com.momentum.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.momentum.app.model.forms.EntradaDiario

@Database(entities = [EntradaDiario::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entradaDiarioDao(): EntradaDiarioDao
}
