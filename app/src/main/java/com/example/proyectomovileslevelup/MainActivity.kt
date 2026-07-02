package com.example.proyectomovileslevelup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.proyectomovileslevelup.Navigation.Navegacion
import com.example.proyectomovileslevelup.ui.theme.ProyectoMovilesLevelUPTheme
//firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestoreSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- CONFIGURACIÓN DE FIRESTORE ---
        val settings = firestoreSettings {
            isPersistenceEnabled = true // Habilita la caché local
        }
        FirebaseFirestore.getInstance().firestoreSettings = settings
        // ----------------------------------

        enableEdgeToEdge()
        setContent {
            ProyectoMovilesLevelUPTheme {
                Navegacion()
            }
        }
    }
}
