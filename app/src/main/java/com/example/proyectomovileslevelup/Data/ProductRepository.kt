package com.example.proyectomovileslevelup.Data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()

    // 1. Esta función la necesita tu CatalogViewModel para mostrar la lista
    suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = db.collection("products").get().await()
            snapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 2. Esta función la necesita tu ProductDetailViewModel para ver un producto
    suspend fun getProductById(productId: String): Product? {
        return try {
            // Buscamos en la colección 'products' donde el campo 'id' sea igual al que recibimos
            val snapshot = db.collection("products")
                .whereEqualTo("id", productId)
                .get()
                .await()

            // Obtenemos el primer documento que coincida
            snapshot.documents.firstOrNull()?.toObject(Product::class.java)
        } catch (e: Exception) {
            println("DEBUG_REPO: Excepción: ${e.message}")
            null
        }
    }
}