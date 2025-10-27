package com.momentum.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val age: Int? = null,
    val sex: String? = null,
    val maritalStatus: String? = null,
    val occupation: String? = null,
    val phone: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)