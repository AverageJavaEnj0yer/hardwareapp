package com.example.hardwareapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hardwareapp.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
