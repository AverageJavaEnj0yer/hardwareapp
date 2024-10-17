package com.example.hardwareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hardwareapp.data.User


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import com.example.hardwareapp.data.Order
import com.example.hardwareapp.data.UserDao
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import org.json.JSONObject



@Composable
fun UserProfileScreen(
    userDao: UserDao,
    user: User,
    onLogout: () -> Unit,
    onAddProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var orders by remember { mutableStateOf(emptyList<Order>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(user) {
        coroutineScope.launch {
            orders = userDao.getOrdersByUserId(user.id)
        }
    }

    val primaryColor = Color(0xFF6200EE) // Primary purple color
    val textColor = Color(0xFF000000) // Black text color
    val secondaryTextColor = Color(0xFF757575) // Gray text color

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        shape = RoundedCornerShape(12.dp),
                        color = primaryColor.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.username.first().toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = primaryColor
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(user.username, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Text("ID: ${user.id}", style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выйти из аккаунта")
        }

        if (user.isAdmin) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddProductClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить товар")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("История заказов:", style = MaterialTheme.typography.titleMedium)

        if (orders.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Здесь пока ничего нет", style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(orders) { order ->
                    OrderItem(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: Order) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(order.timestamp)
    var showDetails by remember { mutableStateOf(false) }

    val orderDetails = JSONObject(order.orderDetails)
    val userId = orderDetails.getInt("userId")
    val username = orderDetails.getString("username")
    val items = orderDetails.getJSONObject("items")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Заказ №${order.id}", style = MaterialTheme.typography.titleMedium)
            Text("Дата: $formattedDate", style = MaterialTheme.typography.bodyMedium)
            Text("Общая стоимость: ${String.format("%.2f", order.totalPrice)} BYN", style = MaterialTheme.typography.bodyMedium)

            if (showDetails) {
                Text("Пользователь: $username (ID: $userId)", style = MaterialTheme.typography.bodyMedium)
                Text("Товары:", style = MaterialTheme.typography.bodyMedium)
                items.keys().forEach { key ->
                    val price = items.getDouble(key)
                    Text("$key: ${String.format("%.2f", price)} BYN", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(
                onClick = { showDetails = !showDetails },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (showDetails) "Скрыть" else "Подробнее")
            }
        }
    }
}
