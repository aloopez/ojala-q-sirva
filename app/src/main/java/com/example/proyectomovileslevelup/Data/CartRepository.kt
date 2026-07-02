package com.example.proyectomovileslevelup.Data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object CartRepository {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addProduct(product: Product) {
        _cartItems.update { items ->
            val existingItem = items.find { it.product.id == product.id }
            if (existingItem != null) {
                items.map { 
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it 
                }
            } else {
                items + CartItem(product, 1)
            }
        }
    }

    fun updateQuantity(productId: String, delta: Int) {
        _cartItems.update { items ->
            items.map { item ->
                if (item.product.id == productId) {
                    val newQuantity = (item.quantity + delta).coerceAtLeast(1)
                    item.copy(quantity = newQuantity)
                } else {
                    item
                }
            }
        }
    }

    fun removeItem(productId: String) {
        _cartItems.update { items ->
            items.filter { it.product.id != productId }
        }
    }

    fun getTotal(): Double {
        return _cartItems.value.sumOf { it.product.price * it.quantity }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }
}
