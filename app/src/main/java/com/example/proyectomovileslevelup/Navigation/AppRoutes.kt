package com.example.proyectomovileslevelup.Navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoutes {
    @Serializable
    data object Home : AppRoutes()

    @Serializable
    data class Catalog(val category: String = "Todas", val navId: Long = 0) : AppRoutes()

    @Serializable
    data object Cart : AppRoutes()

    @Serializable
    data object Profile : AppRoutes()
    @Serializable
    data object Login : AppRoutes()
    @Serializable
    data object PurchaseHistory : AppRoutes()
    @Serializable
    data object ChangePassword : AppRoutes()

    @Serializable
    data class ProductDetail(val productId: String) : AppRoutes()
}
