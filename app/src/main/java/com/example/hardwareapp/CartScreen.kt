package com.example.hardwareapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hardwareapp.data.Product
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Delete

@Composable
fun CartScreen(cartItems: List<Product>, onRemoveFromCart: (Product) -> Unit, onCheckout: () -> Unit) {
    val orangeColor = Color(0xFFFFA500) // Определение оранжевого цвета
    val totalPrice = cartItems.sumOf { it.price }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Корзина", style = MaterialTheme.typography.headlineMedium)

        if (cartItems.isEmpty()) {
            Text("Корзина пуста", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { product ->
                    CartItem(product = product, onRemoveFromCart = onRemoveFromCart)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Итого: ${totalPrice} BYN", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCheckout,
                colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Оформить заказ", color = Color.White)
            }
        }
    }
}

@Composable
fun CartItem(product: Product, onRemoveFromCart: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "${product.price} BYN", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            }
            IconButton(onClick = { onRemoveFromCart(product) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove from cart")
            }
        }
    }
}
