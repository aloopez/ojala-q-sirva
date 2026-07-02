// AutenViewModel.kt
package com.example.proyectomovileslevelup.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectomovileslevelup.Data.Auth.AutenticacionRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class NavigationState {
    data object Unauthenticated : NavigationState()
    data object Authenticated : NavigationState()
}

class AutenViewModel(private val repository: AutenticacionRepositorio) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Unauthenticated)
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUserEmail = MutableStateFlow("")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    init {
        verificarInicioSesion()
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun verificarInicioSesion() {
        val isLoggedIn = repository.obtenerUsuarioRecurrente()
        _isAuthenticated.value = isLoggedIn

        // Obtener el email del usuario actual si está logueado
        if (isLoggedIn) {
            _currentUserEmail.value = repository.obtenerEmailUsuarioActual() ?: ""
        }

        _navigationState.value = if (isLoggedIn) {
            NavigationState.Authenticated
        } else {
            NavigationState.Unauthenticated
        }
    }

    fun inicioSesion() {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _authState.value = AuthState.Error("Email y contraseña son requeridos")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            repository.iniciarSesionConCorreoContrasenia(
                _email.value,
                _password.value
            ).fold(
                onSuccess = {
                    _authState.value = AuthState.Success("Sesión Iniciada")
                    _isAuthenticated.value = true
                    _navigationState.value = NavigationState.Authenticated
                    _currentUserEmail.value = _email.value
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Error al iniciar sesión"
                    )
                }
            )
        }
    }

    fun registro() {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _authState.value = AuthState.Error("Email y contraseña son requeridos")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            repository.crearUsuarioConCorreoContrasenia(
                _email.value,
                _password.value
            ).fold(
                onSuccess = {
                    _authState.value = AuthState.Success("Usuario registrado exitosamente")
                    _isAuthenticated.value = true
                    _navigationState.value = NavigationState.Authenticated
                    _currentUserEmail.value = _email.value
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Error al registrar usuario"
                    )
                }
            )
        }
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
        _authState.value = AuthState.Idle
        _isAuthenticated.value = false
        _navigationState.value = NavigationState.Unauthenticated
        _email.value = ""
        _password.value = ""
    }

    fun cambiarContrasenia(nuevaContrasenia: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.actualizarContrasenia(nuevaContrasenia).fold(
                onSuccess = {
                    _authState.value = AuthState.Success("Contraseña actualizada correctamente")
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Error al actualizar contraseña. Es posible que debas iniciar sesión de nuevo.")
                }
            )
        }
    }

    fun clearState() {
        _authState.value = AuthState.Idle
    }

    class Factory(private val repository: AutenticacionRepositorio) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AutenViewModel::class.java)) {
                return AutenViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}