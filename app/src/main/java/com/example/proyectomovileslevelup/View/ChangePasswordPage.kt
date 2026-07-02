package com.example.proyectomovileslevelup.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectomovileslevelup.ViewModel.AutenViewModel
import com.example.proyectomovileslevelup.ViewModel.AuthState
import com.example.proyectomovileslevelup.ui.components.TopLogo
import com.example.proyectomovileslevelup.ui.theme.*

@Composable
fun ChangePasswordPage(
    navController: NavController,
    viewModel: AutenViewModel
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopLogo()
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = White)
                }
            }
        },
        containerColor = Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Black)
                .imePadding() // Importante: Eleva el contenido ante el teclado
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AstonSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CAMBIAR CONTRASEÑA",
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nueva Contraseña", color = AstonTextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Silver) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AstonAccentGreen,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedContainerColor = Black,
                            unfocusedContainerColor = Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar Contraseña", color = AstonTextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Silver) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AstonAccentGreen,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedContainerColor = Black,
                            unfocusedContainerColor = Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Ayuda visual inmediata
                    if (newPassword.isNotBlank() && confirmPassword.isNotBlank()) {
                        val passwordsMatch = newPassword == confirmPassword
                        Text(
                            text = if (passwordsMatch) "Las contraseñas coinciden" else "Las contraseñas no coinciden",
                            color = if (passwordsMatch) Color.Green else Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.cambiarContrasenia(newPassword) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = newPassword.isNotBlank() && newPassword == confirmPassword && authState !is AuthState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = AstonRacingGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White)
                        } else {
                            Text("ACTUALIZAR CONTRASEÑA", color = White, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (authState is AuthState.Error) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
            
            // Espacio masivo al final para permitir que el usuario suba la Card
            // totalmente por encima de cualquier teclado móvil.
            Spacer(modifier = Modifier.height(400.dp))
        }
    }
}
