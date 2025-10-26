package com.momentum.app.model.forms

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntradaDiario(
    @PrimaryKey val id: String,
    val titulo: String,
    val contenido: String,
    val estadoEmocional: String,
    val fecha: String,
    val hora: String
)
