package com.example.hardwareapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hardwareapp.data.User
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.background

@Composable
fun UserProfileScreen(user: User, onLogout: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Используем равномерное расстояние между элементами
    ) {
        Text("Профиль пользователя", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Картинка профиля (можно использовать текст для представления пользователя)
        Box(
            modifier = Modifier
                .size(100.dp) // Размер аватара
                .background(
                    shape = RoundedCornerShape(12.dp), // Закругленные углы
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) // Фон аватара
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.username.first().toString(), // Отображаем первую букву имени пользователя
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Информация о пользователе
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Имя пользователя: ${user.username}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("ID: ${user.id}", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onLogout) {
            Text("Выйти из аккаунта")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("История заказов:", style = MaterialTheme.typography.titleMedium)
        // Здесь можно добавить отображение истории заказов, когда она будет готова
    }
}

