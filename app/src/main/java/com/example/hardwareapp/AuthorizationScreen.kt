package com.example.hardwareapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import kotlinx.coroutines.launch
import com.example.hardwareapp.data.UserDao

@Composable
fun AuthorizationScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    userDao: UserDao
) {
    var isRegistration by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope() // Создаем корутинный скоуп

    if (isRegistration) {
        RegistrationScreen(
            onRegistrationSuccess = {
                isRegistration = false // Вернуться к экрану авторизации после регистрации
                onLoginSuccess() // Переход на главный экран
            },
            userDao = userDao
        )
    } else {
        // Экран авторизации
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

            // Обработка нажатия на кнопку "Войти"
            Button(onClick = {
                if (username.isEmpty() || password.isEmpty()) {
                    errorMessage = "Пожалуйста, введите имя пользователя и пароль"
                } else {
                    // Проверка пользователя в базе данных
                    coroutineScope.launch {
                        val user = userDao.getUserByUsernameAndPassword(username, password)
                        if (user != null) {
                            onLoginSuccess() // Успешная авторизация
                        } else {
                            errorMessage = "Неверное имя пользователя или пароль"
                        }
                    }
                }
            }) {
                Text("Войти")
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Нет аккаунта? Зарегистрируйтесь",
                modifier = Modifier.clickable { isRegistration = true }, // Переход к регистрации
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
