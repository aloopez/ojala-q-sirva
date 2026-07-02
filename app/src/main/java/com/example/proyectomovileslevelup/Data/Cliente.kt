package com.example.proyectomovileslevelup.Data

data class Cliente(
    val userId: String = "",
    val email: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
) {
    // Convertir a Map para Firestore
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "email" to email,
            "nombre" to nombre,
            "telefono" to telefono,
            "direccion" to direccion,
            "createdAt" to createdAt
        )
    }

    companion object {
        // Crear desde DocumentSnapshot de Firestore
        fun fromDocument(document: com.google.firebase.firestore.DocumentSnapshot): Cliente {
            return Cliente(
                userId = document.getString("userId") ?: "",
                email = document.getString("email") ?: "",
                nombre = document.getString("nombre") ?: "",
                telefono = document.getString("telefono") ?: "",
                direccion = document.getString("direccion") ?: "",
                createdAt = document.getTimestamp("createdAt") ?: com.google.firebase.Timestamp.now()
            )
        }
    }
}