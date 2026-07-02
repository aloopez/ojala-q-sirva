package com.example.proyectomovileslevelup.Data.Auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.proyectomovileslevelup.Data.Auth.AutenticacionRepositorio
import kotlinx.coroutines.tasks.await

class FireBaseAutenRepositorio() : AutenticacionRepositorio {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun iniciarSesionConCorreoContrasenia(
        email: String,
        password: String
    ): Result<FirebaseUser?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun crearUsuarioConCorreoContrasenia(
        email: String,
        password: String
    ): Result<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun obtenerUsuarioRecurrente(): Boolean {
        return auth.currentUser != null
    }

    override fun obtenerEmailUsuarioActual(): String? {
        return auth.currentUser?.email
    }

    override fun cerrarSesion() {
        auth.signOut()
    }

    override suspend fun actualizarContrasenia(nuevaContrasenia: String): Result<Unit> {
        return try {
            auth.currentUser?.updatePassword(nuevaContrasenia)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
