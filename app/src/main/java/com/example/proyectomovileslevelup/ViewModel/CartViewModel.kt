package com.example.proyectomovileslevelup.ViewModel

import androidx.lifecycle.ViewModel
import com.example.proyectomovileslevelup.Data.CartRepository
import com.example.proyectomovileslevelup.Data.CartItem
import com.example.proyectomovileslevelup.Data.PurchaseRepository

class CartViewModel : ViewModel() {
    val cartItems = CartRepository.cartItems

    fun updateQuantity(productId: String, delta: Int) {
        CartRepository.updateQuantity(productId, delta)
    }

    fun removeItem(productId: String) {
        CartRepository.removeItem(productId)
    }

    fun getTotal(): Double {
        return CartRepository.getTotal()
    }

    fun checkout() {
        if (cartItems.value.isNotEmpty()) {
            PurchaseRepository.addPurchases(cartItems.value)
            CartRepository.clearCart()
        }
    }
}
