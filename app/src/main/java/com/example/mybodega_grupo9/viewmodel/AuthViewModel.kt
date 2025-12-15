package com.example.mybodega_grupo9.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybodega_grupo9.data.AuthRepository
import com.example.mybodega_grupo9.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AuthRepository(app)

    // Estado del usuario actual
    val currentUser: StateFlow<UserData?> = repo.getUserData()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Estados de UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    // ==================== LOGIN ====================
    fun login(email: String, password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repo.login(email, password)

            result.onSuccess { response ->
                _message.value = "Bienvenido, ${response.usuario?.nombre}"
                _loginSuccess.value = true
                onSuccess()
            }.onFailure { error ->
                _message.value = error.message ?: "Error al iniciar sesión"
                _loginSuccess.value = false
            }

            _isLoading.value = false
        }
    }

    // ==================== REGISTRO ====================
    fun register(
        email: String,
        password: String,
        nombre: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repo.register(email, password, nombre)

            result.onSuccess {
                _message.value = "Usuario registrado exitosamente"
                onSuccess()
            }.onFailure { error ->
                _message.value = error.message ?: "Error al registrar usuario"
            }

            _isLoading.value = false
        }
    }

    // ==================== LOGOUT ====================
    fun logout(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repo.logout()
            _message.value = "Sesión cerrada"
            onSuccess()
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}