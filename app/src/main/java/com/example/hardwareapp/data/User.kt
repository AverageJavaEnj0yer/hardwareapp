package com.example.hardwareapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Автоинкремент ID
    val username: String,
    val password: String
)
