package com.example.suiverliverson

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.suiverliverson.ui.theme.AppSurface
import com.example.suiverliverson.ui.theme.BlueAccent
import com.example.suiverliverson.ui.theme.LightGray
import com.example.suiverliverson.ui.theme.PinkGradientStart
import com.example.suiverliverson.ui.theme.White

enum class Tab(val route: String, val icon: ImageVector) {
    HOME("home", Icons.Default.Home),
    STATS("stats", Icons.Default.BarChart),
    MAP("map", Icons.Default.Place),
    PROFILE("profile", Icons.Default.Person);

    fun label(s: Strings) = when (this) {
        HOME -> s.home
        STATS -> s.stats
        MAP -> s.map
        PROFILE -> s.profile
    }
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val s = LocalStrings.current
    val context = LocalContext.current
    val store = remember { AccountStore(context) }
    val deliveryManager = remember { DeliveryManager() }

    val entry by nav.currentBackStackEntryAsState()
    val current = entry?.destination?.route
    val showBar = Tab.values().any { it.route == current }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBar) {
                // شريط تنقل زجاجي عائم مرفوع ومريح فوق أزرار نظام التلفون 🚀
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(66.dp)
                            .clip(RoundedCornerShape(33.dp))
                            .border(
                                width = 1.2.dp,
                                brush = Brush.horizontalGradient(
                                    listOf(PinkGradientStart.copy(alpha = 0.4f), BlueAccent.copy(alpha = 0.4f))
                                ),
                                shape = RoundedCornerShape(33.dp)
                            ),
                        color = AppSurface.copy(alpha = 0.95f),
                        tonalElevation = 10.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Tab.values().forEach { tab ->
                                val selected = current == tab.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        nav.navigate(tab.route) {
                                            popUpTo(Tab.HOME.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Box(
                                            modifier = Modifier
                                                .size(if (selected) 38.dp else 24.dp)
                                                .clip(CircleShape)
                                                .background(if (selected) BlueAccent else Color.Transparent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                tab.icon,
                                                contentDescription = null,
                                                tint = if (selected) White else LightGray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            tab.label(s),
                                            color = if (selected) White else LightGray,
                                            fontSize = 10.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "splash",
            modifier = Modifier.padding(padding)
        ) {
            composable("splash") {
                SplashScreen {
                    val startDest = if (store.current() != null) "home" else "login"
                    nav.navigate(startDest) { popUpTo("splash") { inclusive = true } }
                }
            }
            composable("login") {
                LoginScreen(
                    store = store,
                    onLogin = {
                        nav.navigate("home") { popUpTo("login") { inclusive = true } }
                    },
                    onCreateAccount = { nav.navigate("signup") },
                    onForgotPassword = { nav.navigate("forgot") }
                )
            }
            composable("signup") {
                SignUpScreen(
                    store = store,
                    onBack = { nav.popBackStack() },
                    onSuccess = {
                        nav.navigate("home") { popUpTo("login") { inclusive = true } }
                    }
                )
            }
            composable("forgot") {
                ForgotPasswordScreen(
                    store = store,
                    onBack = { nav.popBackStack() }
                )
            }
            composable(Tab.HOME.route) {
                HomeScreen(
                    deliveryManager = deliveryManager,
                    onNavigateToMap = { nav.navigate(Tab.MAP.route) },
                    onNavigateToNotifications = { nav.navigate("notifications") }
                )
            }
            composable(Tab.STATS.route) {
                StatsScreen()
            }
            composable(Tab.MAP.route) {
                MapScreen(deliveryManager = deliveryManager)
            }
            composable(Tab.PROFILE.route) {
                ProfileScreen(
                    store = store,
                    deliveryManager = deliveryManager,
                    onNavigateToSettings = { nav.navigate("settings") },
                    onNavigateToHistory = { nav.navigate("history") },
                    onLogout = {
                        store.logout()
                        nav.navigate("login") { popUpTo("home") { inclusive = true } }
                    }
                )
            }
            composable("notifications") {
                NotificationsScreen(
                    deliveryManager = deliveryManager,
                    onBack = { nav.popBackStack() }
                )
            }
            composable("settings") {
                SettingsScreen(
                    store = store,
                    onBack = { nav.popBackStack() }
                )
            }
            composable("history") {
                HistoryScreen(
                    deliveryManager = deliveryManager,
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}
