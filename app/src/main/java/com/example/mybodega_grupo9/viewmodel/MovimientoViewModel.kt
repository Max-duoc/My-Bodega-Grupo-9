package com.example.mybodega_grupo9.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybodega_grupo9.data.MovimientoRepository
import com.example.mybodega_grupo9.data.local.MovimientoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovimientoViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MovimientoRepository(app)

    val movimientos: StateFlow<List<MovimientoEntity>> = repo.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ==================== SINCRONIZACIÓN ====================

    /**
     * Sincroniza movimientos con el servidor.
     * Llama esto al iniciar la pantalla de movimientos.
     */
    fun syncMovimientos() = viewModelScope.launch {
        _isLoading.value = true
        repo.syncPendingChanges()
        _isLoading.value = false
    }

    // ==================== LIMPIAR HISTORIAL ====================

    fun clearAll() = viewModelScope.launch {
        _isLoading.value = true
        val result = repo.clearAll()
        _isLoading.value = false

        result.onSuccess {
            _message.value = "Historial limpiado"
        }.onFailure { error ->
            _message.value = "Error: ${error.message}"
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    // NOTA: Ya no necesitamos registrarMovimiento() aquí
    // porque el backend lo hace automáticamente cuando
    // se realizan operaciones CRUD en productos
}