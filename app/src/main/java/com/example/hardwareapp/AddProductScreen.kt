package com.example.hardwareapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hardwareapp.data.Product
import com.example.hardwareapp.data.ProductDao
import kotlinx.coroutines.launch

@Composable
fun AddProductScreen(onProductAdded: () -> Unit, productDao: ProductDao) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
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
    var products by remember { mutableStateOf(emptyList<Product>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                            coroutineScope.launch {
                                products = productDao.getProductsByCategory(category)
                            }
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

        Button(onClick = {
            if (selectedCategory.isEmpty() || name.isEmpty() || price.isEmpty() || description.isEmpty()) {
                errorMessage = "Пожалуйста, заполните все поля"
            } else {
                coroutineScope.launch {
                    try {
                        val product = Product(
                            category = selectedCategory,
                            name = name,
                            price = price.toDouble(),
                            description = description
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

        Spacer(modifier = Modifier.height(16.dp))
        Text("Товары в категории: $selectedCategory", style = MaterialTheme.typography.headlineMedium)

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                ProductItem(product = product)
            }
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Цена: ${product.price}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Описание: ${product.description}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
