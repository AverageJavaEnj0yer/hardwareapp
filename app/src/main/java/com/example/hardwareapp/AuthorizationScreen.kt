package com.example.hardwareapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.hardwareapp.data.UserDao
import kotlinx.coroutines.launch
import com.example.hardwareapp.data.User

@Composable
fun AuthorizationScreen(
    onLoginSuccess: (User) -> Unit,
    onRegisterClick: () -> Unit,
    userDao: UserDao
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope() // Создаем корутинный скоуп

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Авторизация", style = MaterialTheme.typography.headlineMedium)

        // Поля для ввода логина и пароля
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Имя пользователя") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (username.isEmpty() || password.isEmpty()) {
                errorMessage = "Пожалуйста, введите имя пользователя и пароль"
            } else {
                // Запускаем корутину
                coroutineScope.launch {
                    // Используем корректный метод для проверки пользователя
                    val user = userDao.getUserByUsername(username)
                    if (user != null && user.password == password) {
                        onLoginSuccess(user) // Успешная авторизация
                    } else {
                        errorMessage = "Неверное имя пользователя или пароль"
                    }
                }
            }
        }) {
            Text("Войти")
        }

        Button(onClick = onRegisterClick, modifier = Modifier.padding(top = 16.dp)) {
            Text("Регистрация")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
