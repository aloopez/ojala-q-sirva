package com.example.proyectomovileslevelup.Data.Auth

import com.google.firebase.auth.FirebaseUser

interface AutenticacionRepositorio {
    fun obtenerUsuarioRecurrente(): Boolean
    suspend fun iniciarSesionConCorreoContrasenia(email: String, password: String): Result<FirebaseUser?>
    suspend fun crearUsuarioConCorreoContrasenia(email: String, password: String): Result<FirebaseUser?>
    fun cerrarSesion()

    fun obtenerEmailUsuarioActual(): String?
    suspend fun actualizarContrasenia(nuevaContrasenia: String): Result<Unit>
}