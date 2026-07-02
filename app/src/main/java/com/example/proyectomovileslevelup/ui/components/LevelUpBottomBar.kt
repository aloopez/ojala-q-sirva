package com.example.proyectomovileslevelup.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.proyectomovileslevelup.Navigation.AppRoutes
import com.example.proyectomovileslevelup.ViewModel.AutenViewModel
import com.example.proyectomovileslevelup.ui.theme.Black
import com.example.proyectomovileslevelup.ui.theme.White
import com.example.proyectomovileslevelup.ui.theme.Silver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun LevelUpBottomBar(navController: NavController, autenViewModel: AutenViewModel) {

    val isAuthenticated by autenViewModel.isAuthenticated.collectAsState()

    val items = listOf(
        NavigationItem("Inicio", Icons.Default.Home, AppRoutes.Home),
        NavigationItem("Catálogo", Icons.Default.ViewList, AppRoutes.Catalog()),
        NavigationItem("Carrito", Icons.Default.ShoppingCart, AppRoutes.Cart),
        NavigationItem(
            title = if (isAuthenticated) "Perfil" else "Cuenta",
            icon = Icons.Default.Person,
            route = if (isAuthenticated) AppRoutes.Profile else AppRoutes.Login
        )
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    NavigationBar(
        containerColor = Black,
        contentColor = White
    ) {
        items.forEach { item ->
            val selected = if (item.route is AppRoutes.Profile || item.route is AppRoutes.Login) {
                currentDestination?.hasRoute(AppRoutes.Profile::class) == true ||
                        currentDestination?.hasRoute(AppRoutes.Login::class) == true
            } else {
                currentDestination?.hasRoute(item.route::class) ?: false
            }

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = item.route !is AppRoutes.Home
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = White,
                    selectedTextColor = White,
                    unselectedIconColor = Silver,
                    unselectedTextColor = Silver,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

private data class NavigationItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: AppRoutes
)
