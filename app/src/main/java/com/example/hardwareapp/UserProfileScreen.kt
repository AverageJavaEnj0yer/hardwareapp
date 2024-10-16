package com.example.hardwareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hardwareapp.data.User


@Composable
fun UserProfileScreen(user: User, onLogout: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Используем равномерное расстояние между элементами
    ) {
        // Полоса с иконкой профиля
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.username.first().toString(), // Отображаем первую букву имени пользователя
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Информация о пользователе
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Имя пользователя: ${user.username}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("ID: ${user.id}", style = MaterialTheme.typography.bodySmall) // Меньший шрифт для ID
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.align(Alignment.End) // Перемещаем кнопку в конец
        ) {
            Text("Выйти из аккаунта")
        }

        Spacer(modifier = Modifier.weight(1f)) // Отодвигаем кнопку вниз
    }
}


