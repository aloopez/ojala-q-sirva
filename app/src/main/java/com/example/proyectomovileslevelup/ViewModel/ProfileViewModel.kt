package com.example.proyectomovileslevelup.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectomovileslevelup.Data.Cliente
import com.example.proyectomovileslevelup.Data.ClienteRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    data object Idle : ProfileState()
    data object Loading : ProfileState()
    data object Saving : ProfileState()
    data class Success(val message: String) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val clienteRepository = ClienteRepository

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _clienteData = MutableStateFlow<Cliente?>(null)
    val clienteData: StateFlow<Cliente?> = _clienteData.asStateFlow()

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _telefono = MutableStateFlow("")
    val telefono: StateFlow<String> = _telefono.asStateFlow()

    private val _direccion = MutableStateFlow("")
    val direccion: StateFlow<String> = _direccion.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _hasUnsavedChanges = MutableStateFlow(false)
    val hasUnsavedChanges: StateFlow<Boolean> = _hasUnsavedChanges.asStateFlow()

    private val _isDataLoaded = MutableStateFlow(false)
    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded.asStateFlow()

    init {
        cargarEmailUsuario()
        cargarDatosUsuario()
    }

    private fun cargarEmailUsuario() {
        _email.value = auth.currentUser?.email ?: ""
    }

    fun cargarDatosUsuario() {
        val userId = auth.currentUser?.uid ?: return

        cargarEmailUsuario()

        viewModelScope.launch {
            _profileState.value = ProfileState.Loading

            clienteRepository.getUserData(userId).fold(
                onSuccess = { cliente ->
                    if (cliente != null) {
                        _clienteData.value = cliente
                        _nombre.value = cliente.nombre
                        _telefono.value = cliente.telefono
                        _direccion.value = cliente.direccion
                    } else {
                        crearDocumentoInicial()
                    }
                    _isDataLoaded.value = true
                    _profileState.value = ProfileState.Idle
                },
                onFailure = { exception ->
                    _profileState.value = ProfileState.Error(
                        exception.message ?: "Error al cargar datos"
                    )
                }
            )
        }
    }

    fun onNombreChange(newValue: String) {
        _nombre.value = newValue
        if (_profileState.value is ProfileState.Error) {
            _profileState.value = ProfileState.Idle
        }
        verificarCambios()
    }

    fun onTelefonoChange(newValue: String) {
        val digitsOnly = newValue.filter { it.isDigit() }
        _telefono.value = if (digitsOnly.length == 8) {
            "${digitsOnly.take(4)}-${digitsOnly.drop(4)}"
        } else {
            digitsOnly
        }
        if (_profileState.value is ProfileState.Error) {
            _profileState.value = ProfileState.Idle
        }
        verificarCambios()
    }

    fun onDireccionChange(newValue: String) {
        _direccion.value = newValue
        if (_profileState.value is ProfileState.Error) {
            _profileState.value = ProfileState.Idle
        }
        verificarCambios()
    }

    private fun verificarCambios() {
        val original = _clienteData.value

        if (original == null) {
            _hasUnsavedChanges.value = _nombre.value.isNotBlank() ||
                    _telefono.value.isNotBlank() ||
                    _direccion.value.isNotBlank()
        } else {
            _hasUnsavedChanges.value =
                _nombre.value != original.nombre ||
                        _telefono.value != original.telefono ||
                        _direccion.value != original.direccion
        }
    }

    fun guardarDatosUsuario() {
        val userId = auth.currentUser?.uid ?: return
        val userEmail = _email.value

        viewModelScope.launch {
            _profileState.value = ProfileState.Saving

            val cliente = Cliente(
                userId = userId,
                email = userEmail,
                nombre = _nombre.value.trim(),
                telefono = _telefono.value.trim(),
                direccion = _direccion.value.trim()
            )

            clienteRepository.saveUserData(cliente).fold(
                onSuccess = {
                    _clienteData.value = cliente
                    _hasUnsavedChanges.value = false
                    _profileState.value = ProfileState.Success("Datos guardados correctamente")
                },
                onFailure = { exception ->
                    _profileState.value = ProfileState.Error(
                        exception.message ?: "Error al guardar datos"
                    )
                }
            )
        }
    }

    private fun crearDocumentoInicial() {
        val userId = auth.currentUser?.uid ?: return
        val userEmail = auth.currentUser?.email ?: ""

        viewModelScope.launch {
            val cliente = Cliente(
                userId = userId,
                email = userEmail,
                nombre = "",
                telefono = "",
                direccion = ""
            )

            clienteRepository.saveUserData(cliente).fold(
                onSuccess = {
                    _clienteData.value = cliente
                    _isDataLoaded.value = true
                    _nombre.value = ""
                    _telefono.value = ""
                    _direccion.value = ""
                },
                onFailure = { }
            )
        }
    }

    fun clearState() {
        _profileState.value = ProfileState.Idle
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}