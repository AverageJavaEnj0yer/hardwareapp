package com.example.hardwareapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Query("SELECT * FROM Product")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM Product WHERE category = :category")
    suspend fun getProductsByCategory(category: String): List<Product>
}