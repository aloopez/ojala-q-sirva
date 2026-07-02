package com.example.proyectomovileslevelup.Data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PurchaseRepository {
    private val _purchases = MutableStateFlow<List<Compra>>(emptyList())
    val purchases: StateFlow<List<Compra>> = _purchases.asStateFlow()

    fun addPurchases(cartItems: List<CartItem>) {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val newPurchases = cartItems.map { item ->
            Compra(
                fecha = currentDate,
                producto = item.product.name,
                precio = item.product.price * item.quantity
            )
        }
        _purchases.update { current -> newPurchases + current }
    }
}

data class Compra(val fecha: String, val producto: String, val precio: Double)
