package com.error404.mundialtpi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.error404.mundialtpi.models.LoginRequest
import com.error404.mundialtpi.models.RegistroRequest
import com.error404.mundialtpi.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    var isLoggedIn by mutableStateOf(false)
        private set

    var nombreUsuario by mutableStateOf("Usuario")
        private set

    init {
        viewModelScope.launch {
            repository.isLoggedInFlow().collect { status ->
                isLoggedIn = status
            }
        }
        viewModelScope.launch {
            repository.getNombreUsuarioFlow().collect { nombre ->
                nombreUsuario = nombre
            }
        }
    }

    fun login(email: String, contrasena: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(LoginRequest(email, password = contrasena))
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Error de login")
            }
        }
    }

    fun loginConQR(qrDataStr: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val jsonObject = org.json.JSONObject(qrDataStr)
                val token = jsonObject.getString("token")
                val nombre = jsonObject.getString("nombre")

                repository.guardarToken(token, nombre)

                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Formato de QR incorrecto")
            }
        }
    }

    fun registro(nombre: String, email: String, contrasena: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.registro(RegistroRequest(nombre, email, password = contrasena))
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Error de registro")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}