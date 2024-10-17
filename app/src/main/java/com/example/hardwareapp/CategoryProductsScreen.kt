package com.example.hardwareapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hardwareapp.data.Product
import com.example.hardwareapp.data.ProductDao
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage


@Composable
fun CategoryProductsScreen(category: String, productDao: ProductDao, onBackClick: () -> Unit) {
    var products by remember { mutableStateOf(emptyList<Product>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(category) {
        coroutineScope.launch {
            products = productDao.getProductsByCategory(category)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Товары в категории: $category", style = MaterialTheme.typography.headlineMedium)
        }

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
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Цена: ${product.price}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Описание: ${product.description}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}