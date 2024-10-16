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

@Composable
fun RegistrationScreen(onRegistrationSuccess: () -> Unit, onCancelClick: () -> Unit, userDao: UserDao) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Регистрация", style = MaterialTheme.typography.headlineMedium)

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
                coroutineScope.launch {
                    val existingUser = userDao.getUserByUsername(username)
                    if (existingUser != null) {
                        errorMessage = "Пользователь с таким именем уже существует"
                    } else {
                        userDao.insert(User(username = username, password = password))
                        onRegistrationSuccess() // Успешная регистрация
                    }
                }
            }
        }) {
            Text("Зарегистрироваться")
        }

        Button(onClick = { onCancelClick() }) {
            Text("Отмена")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

