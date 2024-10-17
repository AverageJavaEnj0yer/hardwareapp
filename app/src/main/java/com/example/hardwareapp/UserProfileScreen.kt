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

@Composable
fun UserProfileScreen(
    user: User,
    onLogout: () -> Unit,
    onAddProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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

        // Здесь можно добавить проверку на пустую историю заказов
        Spacer(modifier = Modifier.height(8.dp))
        Text("Здесь пока ничего нет", style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor))
    }
}
