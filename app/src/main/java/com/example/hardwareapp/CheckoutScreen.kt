package com.example.hardwareapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hardwareapp.data.Order
import com.example.hardwareapp.data.Product
import com.example.hardwareapp.data.User
import com.example.hardwareapp.data.UserDao
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun CheckoutScreen(
    userDao: UserDao,
    currentUser: User,
    cartItems: List<Product>,
    onCheckoutComplete: () -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Оформление заказа", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it },
            label = { Text("Номер карты") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = cvv,
            onValueChange = { cvv = it },
            label = { Text("CVV") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = expiryDate,
            onValueChange = { expiryDate = it },
            label = { Text("Срок действия карты") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (cardNumber.isNotEmpty() && cvv.isNotEmpty() && expiryDate.isNotEmpty()) {
                    coroutineScope.launch {
                        val orderDetails = JSONObject().apply {
                            put("userId", currentUser.id)
                            put("username", currentUser.username)
                            put("items", cartItems.map { it.name to it.price }.toMap())
                        }.toString()

                        val totalPrice = cartItems.sumOf { it.price }

                        val order = Order(
                            userId = currentUser.id,
                            totalPrice = totalPrice,
                            orderDetails = orderDetails
                        )

                        userDao.insertOrder(order)
                        userDao.clearCart(currentUser.id)
                        onCheckoutComplete()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Подтвердить заказ")
        }
    }
}
