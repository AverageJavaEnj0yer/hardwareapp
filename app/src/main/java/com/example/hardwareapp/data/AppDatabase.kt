package com.example.hardwareapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, Product::class, Cart::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
}


