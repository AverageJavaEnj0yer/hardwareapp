package com.example.hardwareapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.hardwareapp.data.User
import com.example.hardwareapp.data.UserDao
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    onRegistrationSuccess: (User) -> Unit,
    onCancelClick: () -> Unit,
    userDao: UserDao
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val primaryColor = Color(0xFF2196F3) // Primary blue color
    val textColor = Color(0xFF000000) // Black text color
    val secondaryTextColor = Color(0xFF757575) // Gray text color

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (screenWidth > 600.dp) 32.dp else 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Регистрация", style = MaterialTheme.typography.headlineMedium, color = primaryColor)

        Spacer(modifier = Modifier.height(16.dp))

        // Поля для ввода логина и пароля
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Имя пользователя") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = primaryColor,
                unfocusedIndicatorColor = secondaryTextColor,
                cursorColor = primaryColor
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = primaryColor,
                unfocusedIndicatorColor = secondaryTextColor,
                cursorColor = primaryColor
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.isEmpty() || password.isEmpty()) {
                    errorMessage = "Пожалуйста, введите имя пользователя и пароль"
                } else {
                    coroutineScope.launch {
                        val existingUser = userDao.getUserByUsername(username)
                        if (existingUser != null) {
                            errorMessage = "Пользователь с таким именем уже существует"
                        } else {
                            // Создание нового пользователя с уникальным ID
                            val newUser = User(username = username, password = password) // ID будет сгенерирован автоматически
                            userDao.insert(newUser)
                            onRegistrationSuccess(newUser) // Успешная регистрация с передачей нового пользователя
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрироваться")
        }

        Button(
            onClick = { onCancelClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Отмена")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = Color.Red)
        }
    }
}
