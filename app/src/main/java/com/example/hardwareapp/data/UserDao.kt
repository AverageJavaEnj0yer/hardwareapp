package com.example.hardwareapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM User WHERE username = :username AND password = :password LIMIT 1")
    suspend fun getUserByUsernameAndPassword(username: String, password: String): User?

    @Query("SELECT * FROM User WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Update
    suspend fun updateUser(user: User)

    @Transaction
    @Query("SELECT * FROM Product WHERE id IN (SELECT productId FROM cart WHERE userId = :userId)")
    suspend fun getCartProducts(userId: Int): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cart: Cart)

    @Query("DELETE FROM cart WHERE userId = :userId AND productId = :productId")
    suspend fun removeFromCart(userId: Int, productId: Int)

    @Query("DELETE FROM cart WHERE userId = :userId")
    suspend fun clearCart(userId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Query("SELECT * FROM `Order` WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getOrdersByUserId(userId: Int): List<Order>
}
