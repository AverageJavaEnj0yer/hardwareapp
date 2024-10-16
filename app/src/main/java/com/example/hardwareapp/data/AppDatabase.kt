package com.example.hardwareapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, Product::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
}


