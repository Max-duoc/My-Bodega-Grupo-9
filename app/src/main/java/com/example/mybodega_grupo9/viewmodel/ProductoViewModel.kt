package com.example.mybodega_grupo9.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybodega_grupo9.data.ProductoRepository
import com.example.mybodega_grupo9.data.local.ProductoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductoViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = ProductoRepository(app)

    val productos = repo.getAll().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Estado para mensajes de error/éxito
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ==================== SINCRONIZACIÓN ====================

    /**
     * Sincroniza productos con el servidor.
     * Llama esto al iniciar la app o cuando detectes conexión.
     */
    fun syncProductos() = viewModelScope.launch {
        repo.syncPendingChanges()
    }

    // ==================== AGREGAR PRODUCTO ====================

    fun agregarProducto(p: ProductoEntity, onSuccess: () -> Unit = {}) = viewModelScope.launch {
        _isLoading.value = true
        val result = repo.insert(p)
        _isLoading.value = false

        result.onSuccess {
            _message.value = "Producto agregado exitosamente"
            onSuccess()
        }.onFailure { error ->
            _message.value = "Error: ${error.message}"
        }
    }

    // ==================== ACTUALIZAR PRODUCTO ====================

    fun actualizarProducto(p: ProductoEntity, onSuccess: () -> Unit = {}) = viewModelScope.launch {
        _isLoading.value = true
        val result = repo.update(p)
        _isLoading.value = false

        result.onSuccess {
            _message.value = "Producto actualizado"
            onSuccess()
        }.onFailure { error ->
            _message.value = "Error: ${error.message}"
        }
    }

    // ==================== ELIMINAR PRODUCTO ====================

    fun eliminarProducto(p: ProductoEntity) = viewModelScope.launch {
        _isLoading.value = true
        val result = repo.delete(p)
        _isLoading.value = false

        result.onSuccess {
            _message.value = "Producto eliminado"
        }.onFailure { error ->
            _message.value = "Error: ${error.message}"
        }
    }

    // ==================== CONSUMIR ====================

    fun consumirProducto(producto: ProductoEntity) = viewModelScope.launch {
        _isLoading.value = true
        val result = repo.consumirProducto(producto)
        _isLoading.value = false

        result.onSuccess {
            _message.value = "Stock consumido"
        }.onFailure { error ->
            _message.value = error.message
        }
    }

    // ==================== REABASTECER ====================

    fun reabastecerProducto(producto: ProductoEntity) = viewModelScope.launch {
        _isLoading.value = true
        val result = repo.reabastecerProducto(producto)
        _isLoading.value = false

        result.onSuccess {
            _message.value = "Stock reabastecido"
        }.onFailure { error ->
            _message.value = error.message
        }
    }

    // Limpiar mensajes
    fun clearMessage() {
        _message.value = null
    }
}