package com.example.proyectomovileslevelup.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovileslevelup.Data.Product
import com.example.proyectomovileslevelup.Data.ProductRepository
import com.example.proyectomovileslevelup.Data.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    // Necesitamos una instancia del repositorio
    private val repository = ProductRepository()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            // Ahora esperamos a que el repositorio traiga el dato
            _product.value = repository.getProductById(productId)
        }
    }

    fun addToCart(product: Product) {
        CartRepository.addProduct(product)
    }
}
