package com.example.proyectomovileslevelup.View

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectomovileslevelup.Navigation.AppRoutes
import com.example.proyectomovileslevelup.ViewModel.AutenViewModel
import com.example.proyectomovileslevelup.ViewModel.ProfileState
import com.example.proyectomovileslevelup.ViewModel.ProfileViewModel
import com.example.proyectomovileslevelup.ui.components.TopLogo
import com.example.proyectomovileslevelup.ui.theme.*
import kotlinx.coroutines.launch

// ProfilePage.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    navController: NavController,
    autenViewModel: AutenViewModel,
    perfilViewModel: ProfileViewModel
) {
    val email by perfilViewModel.email.collectAsState()
    val nombre by perfilViewModel.nombre.collectAsState()
    val telefono by perfilViewModel.telefono.collectAsState()
    val direccion by perfilViewModel.direccion.collectAsState()
    val profileState by perfilViewModel.profileState.collectAsState()
    val hasUnsavedChanges by perfilViewModel.hasUnsavedChanges.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar datos al entrar
    LaunchedEffect(Unit) {
        perfilViewModel.cargarDatosUsuario()
    }

    // Mostrar snackbar
    LaunchedEffect(profileState) {
        when (profileState) {
            is ProfileState.Success -> {
                snackbarHostState.showSnackbar((profileState as ProfileState.Success).message)
                perfilViewModel.clearState()
            }

            is ProfileState.Error -> {
                snackbarHostState.showSnackbar((profileState as ProfileState.Error).message)
                perfilViewModel.clearState()
            }

            else -> {}
        }
    }

    // Variables locales
    var inputName by remember { mutableStateOf(nombre) }
    var inputPhone by remember { mutableStateOf(telefono) }
    var inputAddress by remember { mutableStateOf(direccion) }

    // Sincronizar cuando se cargan datos
    LaunchedEffect(nombre, telefono, direccion) {
        inputName = nombre
        inputPhone = telefono
        inputAddress = direccion
    }

    val isSaving = profileState is ProfileState.Saving

    Scaffold(
        topBar = {
            Column {
                TopLogo()
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = White
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AstonSurface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Avatar
                        Surface(
                            shape = CircleShape,
                            color = Black,
                            modifier = Modifier.size(100.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = AstonRacingGreen,
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxSize()
                            )
                        }

                        Text(
                            text = if (nombre.isNotBlank()) "¡Hola, $nombre!" else "MI CUENTA",
                            color = White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        HorizontalDivider(color = Color.DarkGray)

                        // Email (Read Only)
                        OutlinedTextField(
                            value = email.ifEmpty { "Cargando..." },
                            onValueChange = { },
                            label = { Text("Correo Electrónico", color = AstonTextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = Silver) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (email.isEmpty()) Color.Red else AstonSurface,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = White,
                                unfocusedTextColor = White,
                                focusedContainerColor = Black,
                                unfocusedContainerColor = Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // Nombre
                        OutlinedTextField(
                            value = inputName,
                            onValueChange = {
                                inputName = it
                                perfilViewModel.onNombreChange(it)
                            },
                            label = { Text("Nombre de usuario", color = AstonTextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = Silver) },
                            enabled = !isSaving,
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

                        // Teléfono
                        OutlinedTextField(
                            value = inputPhone,
                            onValueChange = {
                                inputPhone = it
                                perfilViewModel.onTelefonoChange(it)
                            },
                            label = { Text("Número de teléfono", color = AstonTextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = Silver) },
                            prefix = { Text("+503 ", color = White) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            enabled = !isSaving,
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

                        // Dirección
                        OutlinedTextField(
                            value = inputAddress,
                            onValueChange = {
                                inputAddress = it
                                perfilViewModel.onDireccionChange(it)
                            },
                            label = { Text("Dirección de envío", color = AstonTextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Place, null, tint = Silver) },
                            enabled = !isSaving,
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

                        // Botón Guardar
                        if (hasUnsavedChanges) {
                            Button(
                                onClick = { perfilViewModel.guardarDatosUsuario() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                enabled = !isSaving,
                                colors = ButtonDefaults.buttonColors(containerColor = AstonAccentGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = White
                                    )
                                } else {
                                    Text(
                                        "GUARDAR CAMBIOS",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Botón Cerrar Sesión
                        TextButton(
                            onClick = {
                                autenViewModel.cerrarSesion()
                                navController.navigate(AppRoutes.Home) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cerrar Sesión", color = Color.Red, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
