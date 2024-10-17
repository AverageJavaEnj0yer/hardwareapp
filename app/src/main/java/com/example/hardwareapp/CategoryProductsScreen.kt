package com.example.hardwareapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CategoryProductsScreen(
    category: String,
    productDao: ProductDao,
    onBackClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    cartItems: List<Product>
) {
    var products by remember { mutableStateOf(emptyList<Product>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(category) {
        coroutineScope.launch {
            products = productDao.getProductsByCategory(category)
        }
    }

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600

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
                ProductItem(product = product, onProductClick = onProductClick, onAddToCart = onAddToCart, isInCart = cartItems.contains(product), isTablet = isTablet)
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onProductClick: (Product) -> Unit, onAddToCart: (Product) -> Unit, isInCart: Boolean, isTablet: Boolean) {
    val orangeColor = Color(0xFFFFA500) // Определение оранжевого цвета
    val grayColor = Color.Gray // Определение серого цвета

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onProductClick(product) },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = "${product.price} BYN", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                }
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(if (isTablet) 120.dp else 80.dp)
                )
            }
            Button(
                onClick = { onAddToCart(product) },
                colors = ButtonDefaults.buttonColors(containerColor = if (isInCart) grayColor else orangeColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(if (isInCart) "Уже в корзине" else "В корзину", color = Color.White)
            }
        }
    }
}


@Composable
fun ProductDetailScreen(product: Product, onBackClick: () -> Unit, onAddToCart: (Product) -> Unit, isInCart: Boolean) {
    val orangeColor = Color(0xFFFFA500) // Определение оранжевого цвета
    val grayColor = Color.Gray // Определение серого цвета

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }
        AsyncImage(
            model = product.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(if (isTablet) 300.dp else 200.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(text = product.name, style = MaterialTheme.typography.headlineMedium)
        Text(text = "${product.price} BYN", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Text(text = "Описание: ${product.description}", style = MaterialTheme.typography.bodyMedium)
        Button(
            onClick = { onAddToCart(product) },
            colors = ButtonDefaults.buttonColors(containerColor = if (isInCart) grayColor else orangeColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(if (isInCart) "Уже в корзине" else "В корзину", color = Color.White)
        }
    }
}


