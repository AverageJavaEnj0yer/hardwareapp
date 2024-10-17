package com.example.hardwareapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.hardwareapp.data.Product
import com.example.hardwareapp.data.ProductDao
import com.example.hardwareapp.data.UserDao
import kotlinx.coroutines.launch

@Composable
fun AddProductScreen(onProductAdded: () -> Unit, onBackClick: () -> Unit, productDao: ProductDao, userDao: UserDao) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") } // Новое поле для ссылки на изображение
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val categories = listOf(
        "Видеокарты",
        "Процессоры",
        "Материнские платы",
        "Оперативная память",
        "Блоки питания",
        "Корпуса",
        "Системы охлаждения",
        "SSD",
        "Жесткие диски"
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null
            )
        }

        Text("Добавить товар", style = MaterialTheme.typography.headlineMedium)

        // Выпадающий список для выбора категории
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = { },
                readOnly = true,
                label = { Text("Категория") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Название") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Цена") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Описание") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Ссылка на изображение") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (selectedCategory.isEmpty() || name.isEmpty() || price.isEmpty() || description.isEmpty() || imageUrl.isEmpty()) {
                errorMessage = "Пожалуйста, заполните все поля"
            } else {
                coroutineScope.launch {
                    try {
                        val product = Product(
                            category = selectedCategory,
                            name = name,
                            price = price.toDouble(),
                            description = description,
                            imageUrl = imageUrl // Добавление ссылки на изображение
                        )
                        productDao.insert(product)
                        onProductAdded()
                    } catch (e: Exception) {
                        errorMessage = "Ошибка при добавлении товара: ${e.message}"
                    }
                }
            }
        }) {
            Text("Добавить товар")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
