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
import com.example.hardwareapp.data.User
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
import com.example.hardwareapp.data.ProductDao

import kotlinx.coroutines.runBlocking


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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(userDao: UserDao, productDao: ProductDao) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var isRegistration by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var isAddingProduct by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Видеокарты") }

    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    if (isLoggedIn) {
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
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    })
                    1 -> CategoryProductsScreen(category = selectedCategory, productDao = productDao)
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

    if (isAddingProduct) {
        AddProductScreen(
            onProductAdded = {
                isAddingProduct = false
            },
            productDao = productDao
        )
    }
}

@Composable
fun ComponentCatalogScreen(onCategoryClick: (String) -> Unit, modifier: Modifier = Modifier) {
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

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(categories.size) { index ->
            CategoryCard(category = categories[index], onClick = { onCategoryClick(categories[index]) })
        }
    }
}

@Composable
fun CategoryCard(category: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
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
