package com.example.proyectomovileslevelup.Data

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val brand: String = "",
    val imageUrl: String = ""
)

data class CartItem(
    val product: Product,
    val quantity: Int
)
