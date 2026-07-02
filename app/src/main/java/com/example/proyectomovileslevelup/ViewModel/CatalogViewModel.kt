package com.example.proyectomovileslevelup.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectomovileslevelup.Data.Product
import com.example.proyectomovileslevelup.Data.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.regex.Pattern

class CatalogViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {

    // Función auxiliar para normalizar texto (quitar acentos y pasar a minúsculas)
    private fun String.normalize(): String {
        val nfdNormalizedString = Normalizer.normalize(this, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("").lowercase().trim()
    }

    // 1. Empezamos con una lista vacía hasta que Firebase responda
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        // 2. Cargamos los datos al iniciar
        viewModelScope.launch {
            _products.value = repository.getProducts()
        }
    }

    // ... (Mantén tus otros estados _selectedCategory, _searchQuery, etc. igual)
    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedBrand = MutableStateFlow("Todas")
    val selectedBrand: StateFlow<String> = _selectedBrand.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _priceRange = MutableStateFlow(0f..2000f)
    val priceRange: StateFlow<ClosedFloatingPointRange<Float>> = _priceRange.asStateFlow()

    // 3. Generamos las categorías dinámicamente desde la base de datos para asegurar compatibilidad
    val categories: StateFlow<List<String>> = _products.map { list ->
        val dbCategories = list.map { it.category }.distinct().filter { it.isNotBlank() }.sorted()
        if (dbCategories.isEmpty()) {
            listOf("Todas", "Procesadores", "Tarjetas Gráficas", "Memoria RAM", "Almacenamiento", "Placas Madre", "Refrigeración", "Fuentes de Poder", "Gabinetes")
        } else {
            listOf("Todas") + dbCategories
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, listOf("Todas"))

    // 4. Nota: brands ahora se calculará después de que carguen los productos
    val brands: StateFlow<List<String>> = _products.map { list ->
        listOf("Todas") + list.map { it.brand }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.Lazily, listOf("Todas"))

    val filteredProducts = combine(_products, _selectedCategory, _selectedBrand, _searchQuery, _priceRange) { products, category, brand, query, range ->
        products.filter { product ->
            // Filtro de Categoría ultra-flexible: Ignora acentos, mayúsculas y plurales
            val normCategory = category.normalize().removeSuffix("es").removeSuffix("s")
            val normProdCategory = product.category.normalize().removeSuffix("es").removeSuffix("s")
            
            val matchCategory = if (category == "Todas") true 
                               else normProdCategory.contains(normCategory) || normCategory.contains(normProdCategory)
            
            val matchBrand = if (brand == "Todas") true 
                             else product.brand.normalize() == brand.normalize()
            
            val matchPrice = product.price >= range.start && product.price <= range.endInclusive
            
            val matchQuery = product.name.contains(query, ignoreCase = true) || 
                             product.description.contains(query, ignoreCase = true)

            matchCategory && matchBrand && matchPrice && matchQuery
        }
    }

    // ... (Mantén tus funciones selectCategory, updateSearchQuery, etc. iguales)
    fun selectCategory(category: String) { _selectedCategory.value = category }
    fun selectBrand(brand: String) { _selectedBrand.value = brand }
    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updatePriceRange(range: ClosedFloatingPointRange<Float>) { _priceRange.value = range }
}

class CatalogViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CatalogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}