package com.example.hardwareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.hardwareapp.data.AppDatabase
import com.example.hardwareapp.data.UserDao
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable

class MainActivity : ComponentActivity() {
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Инициализация базы данных
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()

        userDao = db.userDao() // Получаем экземпляр DAO

        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }
            var isRegistration by remember { mutableStateOf(false) } // Состояние для экрана регистрации
            var selectedTab by remember { mutableStateOf("home") }

            if (isLoggedIn) {
                // Показать главный экран после успешной авторизации
                Scaffold(
                    bottomBar = {
                        NavigationBar(selectedTab = selectedTab) { tab ->
                            selectedTab = tab
                        }
                    }
                ) { paddingValues ->
                    when (selectedTab) {
                        "home" -> ComponentCatalogScreen(modifier = Modifier.padding(paddingValues))
                        "cart" -> CartScreen(modifier = Modifier.padding(paddingValues))
                        "profile" -> ProfileScreen(modifier = Modifier.padding(paddingValues))
                    }
                }
            } else {
                if (isRegistration) {
                    // Показать экран регистрации
                    RegistrationScreen(
                        onRegistrationSuccess = {
                            isRegistration = false // Возвращение к экрану авторизации после успешной регистрации
                        },
                        onCancelClick = {
                            isRegistration = false // Возвращение к экрану авторизации при отмене
                        },
                        userDao = userDao
                    )
                } else {
                    // Показать экран авторизации
                    AuthorizationScreen(
                        onLoginSuccess = { isLoggedIn = true },
                        onRegisterClick = { isRegistration = true }, // Переход на экран регистрации
                        userDao = userDao // Передаем UserDao в AuthorizationScreen
                    )
                }

            }
        }
    }
}

@Composable
fun ComponentCatalogScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(componentCategories.size) { index ->
            CategoryCard(category = componentCategories[index])
        }
    }
}

@Composable
fun CategoryCard(category: ComponentCategory) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { /* Обработка нажатия на категорию */ },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun NavigationBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == "home",
            onClick = { onTabSelected("home") },
            label = { Text("Каталог") },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) }
        )
        NavigationBarItem(
            selected = selectedTab == "cart",
            onClick = { onTabSelected("cart") },
            label = { Text("Корзина") },
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) }
        )
        NavigationBarItem(
            selected = selectedTab == "profile",
            onClick = { onTabSelected("profile") },
            label = { Text("Профиль") },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) }
        )
    }
}

@Composable
fun CartScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Корзина пока пуста",
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        textAlign = TextAlign.Center,
        fontSize = 20.sp
    )
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Профиль пользователя",
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        textAlign = TextAlign.Center,
        fontSize = 20.sp
    )
}

data class ComponentCategory(val name: String)

val componentCategories = listOf(
    ComponentCategory("Видеокарты"),
    ComponentCategory("Процессоры"),
    ComponentCategory("Материнские платы"),
    ComponentCategory("Оперативная память"),
    ComponentCategory("Блоки питания"),
    ComponentCategory("Корпуса"),
    ComponentCategory("Системы охлаждения"),  // Новая категория
    ComponentCategory("SSD"),                // Новая категория
    ComponentCategory("Жесткие диски")       // Новая категория
)
