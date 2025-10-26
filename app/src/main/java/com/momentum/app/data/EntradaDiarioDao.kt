package com.momentum.app.data

import androidx.room.*
import com.momentum.app.model.forms.EntradaDiario

@Dao
interface EntradaDiarioDao {
    @Query("SELECT * FROM EntradaDiario ORDER BY fecha DESC, hora DESC")
    suspend fun getAll(): List<EntradaDiario>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entrada: EntradaDiario)

    @Delete
    suspend fun delete(entrada: EntradaDiario)
}
