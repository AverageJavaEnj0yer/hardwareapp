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
import com.example.hardwareapp.data.Product
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.example.hardwareapp.data.User
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.example.hardwareapp.data.ProductDao

import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    private lateinit var userDao: UserDao
    private lateinit var productDao: ProductDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()

        userDao = db.userDao()
        productDao = db.productDao()

        // Initialize the admin user
        runBlocking {
            val adminUser = userDao.getUserByUsername("admin")
            if (adminUser == null) {
                userDao.insert(User(username = "admin", password = "admin", isAdmin = true))
            }
        }

        setContent {
            MainScreen(userDao, productDao)
        }
    }
}

@Composable
fun MainScreen(userDao: UserDao, productDao: ProductDao) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var isRegistration by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var isAddingProduct by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var cartItems by remember { mutableStateOf<List<Product>>(emptyList()) }

    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    fun addToCart(product: Product) {
        cartItems = cartItems + product
    }

    fun removeFromCart(product: Product) {
        cartItems = cartItems - product
    }

    if (isLoggedIn) {
        if (isAddingProduct) {
            AddProductScreen(
                onProductAdded = {
                    isAddingProduct = false
                },
                onBackClick = {
                    isAddingProduct = false
                },
                productDao = productDao
            )
        } else if (selectedProduct != null) {
            ProductDetailScreen(
                product = selectedProduct!!,
                onBackClick = {
                    selectedProduct = null
                },
                onAddToCart = { product ->
                    addToCart(product)
                },
                isInCart = cartItems.contains(selectedProduct)
            )
        } else if (selectedCategory != null) {
            CategoryProductsScreen(
                category = selectedCategory!!,
                productDao = productDao,
                onBackClick = {
                    selectedCategory = null
                },
                onProductClick = { product ->
                    selectedProduct = product
                },
                onAddToCart = { product ->
                    addToCart(product)
                },
                cartItems = cartItems
            )
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar(selectedTab = pagerState.currentPage) { page ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    }
                }
            ) { paddingValues ->
                HorizontalPager(
                    count = 3,
                    state = pagerState,
                    modifier = Modifier.padding(paddingValues)
                ) { page ->
                    when (page) {
                        0 -> ComponentCatalogScreen(onCategoryClick = { category ->
                            selectedCategory = category
                        })
                        1 -> CartScreen(
                            cartItems = cartItems,
                            onRemoveFromCart = { product ->
                                removeFromCart(product)
                            },
                            onCheckout = {
                                // Logic for checkout
                            }
                        )
                        2 -> currentUser?.let { user ->
                            UserProfileScreen(
                                user = user,
                                onLogout = {
                                    isLoggedIn = false
                                    currentUser = null
                                },
                                onAddProductClick = {
                                    isAddingProduct = true
                                }
                            )
                        }
                    }
                }
            }
        }
    } else {
        if (isRegistration) {
            RegistrationScreen(
                onRegistrationSuccess = { newUser ->
                    currentUser = newUser
                    isRegistration = false
                },
                onCancelClick = {
                    isRegistration = false
                },
                userDao = userDao
            )
        } else {
            AuthorizationScreen(
                onLoginSuccess = { user ->
                    isLoggedIn = true
                    currentUser = user
                },
                onRegisterClick = { isRegistration = true },
                userDao = userDao
            )
        }
    }
}

@Composable
fun ComponentCatalogScreen(onCategoryClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val categories = listOf(
        "Видеокарты" to "https://imgproxy.onliner.by/POWetnFg-sSHoRExGJim_d8CrOu4bZFJJPy12kO7_uE/w:700/h:550/f:jpg/aHR0cHM6Ly9jb250/ZW50Lm9ubGluZXIu/YnkvY2F0YWxvZy9k/ZXZpY2UvbGFyZ2Uv/NmU1ZjYxMWExNmQ3/ZmY4ZjQ0YzhkN2M1/NzUyNGQ2NzkuanBn",
        "Процессоры" to "https://imgproxy.onliner.by/CnM8oURZJbQugjzfALlpfEcnzobOhZU7yBjQGYt1Ygg/w:170/h:250/f:jpg/aHR0cHM6Ly9jb250/ZW50Lm9ubGluZXIu/YnkvY2F0YWxvZy9k/ZXZpY2Uvb3JpZ2lu/YWwvMzIwMTQ2YWU3/NTAxMDliMGQwMTU2/ZTMyZWY3NTRhNzIu/anBn",
        "Материнские платы" to "https://imgproxy.onliner.by/T8-z4Bhw5S9Wz1GqWSWjuubEJvrjE3eIXGLYp_YWPag/w:700/h:550/f:jpg/aHR0cHM6Ly9jb250/ZW50Lm9ubGluZXIu/YnkvY2F0YWxvZy9k/ZXZpY2UvbGFyZ2Uv/ODg2NmQzNTQ5MWZh/OWNmNDAwMWQxY2M3/OTcwNWFhZmIuanBl/Zw",
        "Оперативная память" to "https://imgproxy.onliner.by/9wXdmYF30dlqFxTryGDGlIkHzrgAVCQPDO-MSpZwWmo/w:700/h:550/f:jpg/aHR0cHM6Ly9jb250/ZW50Lm9ubGluZXIu/YnkvY2F0YWxvZy9k/ZXZpY2UvbGFyZ2Uv/ZjVjNTkxNjA5MzFk/NDg1MGM0ZjRiOWNi/NDMyOGI2YWUuanBl/Zw",
        "Блоки питания" to "https://imgproxy.onliner.by/LzNTO8GrLKzAtHGOzzrPD4Yb2TzzDaCkGBFZDH14Td8/w:700/h:550/f:jpg/aHR0cHM6Ly9jb250/ZW50Lm9ubGluZXIu/YnkvY2F0YWxvZy9k/ZXZpY2UvbGFyZ2Uv/YjEzZTgzM2M3NTVk/MzlhYjc3ZDkzMGVh/MjlmZjRlMjUuanBl/Zw",
        "Корпуса" to "https://imgproxy.onliner.by/ZteIZsL1VtHAeYIp4quOGsv-rbQNNoryrUF71DJ9xZA/w:700/h:550/f:jpg/aHR0cHM6Ly9jb250/ZW50Lm9ubGluZXIu/YnkvY2F0YWxvZy9k/ZXZpY2UvbGFyZ2Uv/MTQ0YTE0OTFiNzBm/MTUyNjA5YjMwNzJi/NThmNTJlYWYuanBn",
        "Системы охлаждения" to "https://imgproxy.onliner.by/kTaVmTJkPk-HBoAfsKyfPMs_IyscqDgEXtVzkZdzKWM/w:700/h:550/f:jpg/aHR0cHM6Ly9jb250/ZW50Lm9ubGluZXIu/YnkvY2F0YWxvZy9k/ZXZpY2UvbGFyZ2Uv/NmIzMzA3ZTNmN2Vm/MzgxMWMxYjgyOGE0/ZDFlYWVkYmMuanBn",
        "SSD" to "https://imgproxy.onliner.by/ppxjL-5KML7YrWugUgR6bLgJHHPs43fpSiqnp4sA8x8/w:700/h:550/f:jpg/aHR0cHM6Ly9jb250/ZW50Lm9ubGluZXIu/YnkvY2F0YWxvZy9k/ZXZpY2UvbGFyZ2Uv/YWQ5MDQ4YzFjOGVk/OWJhMDdjNzllZTA3/MDZjN2NmZWYuanBl/Zw",
        "Жесткие диски" to "https://imgproxy.onliner.by/ZHslhqhfDo3T3wjsSAmJDXT06HGPGHkaejBTpCvlMe4/w:700/h:550/f:jpg/aHR0cHM6Ly9jb250/ZW50Lm9ubGluZXIu/YnkvY2F0YWxvZy9k/ZXZpY2UvbGFyZ2Uv/NWI2OGYwYjEzNTE4/NjU5MDgxNTRhOGFh/NDM0MmRkNWYuanBn"
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(categories.size) { index ->
            val (category, imageUrl) = categories[index]
            CategoryCard(category = category, imageUrl = imageUrl, onClick = { onCategoryClick(category) })
        }
    }
}

@Composable
fun CategoryCard(category: String, imageUrl: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(start = 16.dp)
            )
            Text(
                text = category,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun NavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            label = { Text("Каталог") },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) }
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            label = { Text("Корзина") },
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) }
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
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


data class ComponentCategory(val name: String)

val componentCategories = listOf(
    ComponentCategory("Видеокарты"),
    ComponentCategory("Процессоры"),
    ComponentCategory("Материнские платы"),
    ComponentCategory("Оперативная память"),
    ComponentCategory("Блоки питания"),
    ComponentCategory("Корпуса"),
    ComponentCategory("Системы охлаждения"),
    ComponentCategory("SSD"),
    ComponentCategory("Жесткие диски")
)