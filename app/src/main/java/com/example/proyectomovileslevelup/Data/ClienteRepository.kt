package com.example.proyectomovileslevelup.Data

import com.example.proyectomovileslevelup.Data.Cliente
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ClienteRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    // Guardar o actualizar datos del usuario
    suspend fun saveUserData(cliente: Cliente): Result<Unit> {
        return try {
            usersCollection.document(cliente.userId)
                .set(cliente.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener datos del usuario
    suspend fun getUserData(userId: String): Result<Cliente?> {
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                Result.success(Cliente.fromDocument(document))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar campos específicos
    suspend fun updateUserField(userId: String, field: String, value: Any): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update(field, value)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar datos del usuario (si es necesario)
    suspend fun deleteUserData(userId: String): Result<Unit> {
        return try {
            usersCollection.document(userId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}