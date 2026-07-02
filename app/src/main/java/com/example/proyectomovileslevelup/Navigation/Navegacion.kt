package com.example.proyectomovileslevelup.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.proyectomovileslevelup.Data.Auth.FireBaseAutenRepositorio
import com.example.proyectomovileslevelup.View.CartPage
import com.example.proyectomovileslevelup.View.CatalogPage
import com.example.proyectomovileslevelup.View.HomePage
import com.example.proyectomovileslevelup.View.LoginPage
import com.example.proyectomovileslevelup.View.ProductDetailPage
import com.example.proyectomovileslevelup.View.ProfilePage
import com.example.proyectomovileslevelup.View.ChangePasswordPage
import com.example.proyectomovileslevelup.View.HistorialComprasScreen
import com.example.proyectomovileslevelup.ViewModel.AutenViewModel
import com.example.proyectomovileslevelup.ui.components.ChatBubble
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.proyectomovileslevelup.ViewModel.ProfileViewModel

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    val autenViewModel: AutenViewModel = viewModel(
        factory = AutenViewModel.Factory(FireBaseAutenRepositorio()),
    )
    val perfilViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory()
    )
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {

        NavHost(
            navController = navController,
            startDestination = AppRoutes.Home
        ) {
            composable<AppRoutes.Home> {
                HomePage(navController)
            }

            composable<AppRoutes.Catalog> { backStackEntry ->
                val route: AppRoutes.Catalog = backStackEntry.toRoute()
                CatalogPage(
                    navController = navController, 
                    initialCategory = route.category,
                    navId = route.navId
                )
            }

            composable<AppRoutes.Cart> {
                CartPage(navController)
            }

        composable<AppRoutes.Login> {
            LoginPage(
                navController,
                viewModel = autenViewModel,
            ) {
                navController.navigate(AppRoutes.Home) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

            composable<AppRoutes.Profile> {
                // Placeholder para Perfil/Login
                ProfilePage(
                    navController,
                    autenViewModel = autenViewModel,
                    perfilViewModel = perfilViewModel
                )
            }

            composable<AppRoutes.PurchaseHistory> {
                HistorialComprasScreen(navController)
            }

            composable<AppRoutes.ChangePassword> {
                ChangePasswordPage(navController, autenViewModel)
            }

            composable<AppRoutes.ProductDetail> { backStackEntry ->
                val route: AppRoutes.ProductDetail = backStackEntry.toRoute()
                ProductDetailPage(productId = route.productId, navController = navController)
            }
        }

        //Para la burbuja del chatbot de gemini (oculta en pantalla de Cuenta/Perfil)
        if (currentRoute?.contains("Profile") != true && currentRoute?.contains("Login") != true) {
            ChatBubble()
        }
    }
}
