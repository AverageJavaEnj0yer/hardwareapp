package com.example.hardwareapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val totalPrice: Double,
    val orderDetails: String, // JSON string of the order details
    val timestamp: Long = System.currentTimeMillis()
)
