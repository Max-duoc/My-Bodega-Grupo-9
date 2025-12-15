package com.example.mybodega_grupo9.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybodega_grupo9.data.ProductoRepository
import com.example.mybodega_grupo9.data.SyncResult
import com.example.mybodega_grupo9.data.local.ProductoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductoViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = ProductoRepository(app)

    // Flow automático que se actualiza cuando cambia la BD
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

    // 🔥 Estado de sincronización
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState

    // 🔥 NUEVO: Mapa de estados de productos locales
    private val _productosLocales = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val productosLocales: StateFlow<Map<Int, Boolean>> = _productosLocales

    // 🔥 NUEVO: Estado de carga individual para cada producto
    private val _uploadingProducts = MutableStateFlow<Set<Int>>(emptySet())
    val uploadingProducts: StateFlow<Set<Int>> = _uploadingProducts

    // ==================== VERIFICAR PRODUCTOS LOCALES ====================

    /**
     * Verifica qué productos son locales (no sincronizados).
     * Se llama automáticamente cuando cambian los productos.
     */
    fun checkLocalProducts() = viewModelScope.launch {
        val localMap = mutableMapOf<Int, Boolean>()
        productos.value.forEach { producto ->
            val isLocal = repo.isLocalProduct(producto)
            localMap[producto.id] = isLocal
        }
        _productosLocales.value = localMap
    }

    // ==================== SUBIR PRODUCTO INDIVIDUAL ====================

    /**
     * Sube un producto específico al servidor.
     * Se usa cuando el usuario presiona el botón "Subir" en la card.
     */
    fun uploadProductToServer(producto: ProductoEntity) = viewModelScope.launch {
        // Agregar al set de productos en proceso de subida
        _uploadingProducts.value = _uploadingProducts.value + producto.id

        try {
            val result = repo.uploadProductToServer(producto)

            result.onSuccess {
                _message.value = "✓ '${producto.nombre}' subido al servidor"
                // Actualizar el mapa de productos locales
                _productosLocales.value = _productosLocales.value - producto.id
            }.onFailure { error ->
                _message.value = "Error: ${error.message}"
            }
        } finally {
            // Remover del set de productos en proceso
            _uploadingProducts.value = _uploadingProducts.value - producto.id
        }
    }

    // ==================== SINCRONIZACIÓN BIDIRECCIONAL ====================

    fun syncProductos() = viewModelScope.launch {
        _isLoading.value = true
        _syncState.value = SyncState.Syncing

        try {
            val result = repo.syncPendingChanges()
            _isLoading.value = false

            when (result) {
                is SyncResult.Success -> {
                    _syncState.value = SyncState.Success(
                        uploaded = result.uploaded,
                        downloaded = result.downloaded,
                        updated = result.updated
                    )

                    val messages = mutableListOf<String>()
                    if (result.uploaded > 0) {
                        messages.add("${result.uploaded} subido${if (result.uploaded > 1) "s" else ""}")
                    }
                    if (result.downloaded > 0) {
                        messages.add("${result.downloaded} descargado${if (result.downloaded > 1) "s" else ""}")
                    }
                    if (result.updated > 0) {
                        messages.add("${result.updated} actualizado${if (result.updated > 1) "s" else ""}")
                    }

                    _message.value = if (messages.isNotEmpty()) {
                        "Sincronizado: ${messages.joinToString(", ")}"
                    } else {
                        "Todo está sincronizado"
                    }

                    // Actualizar mapa de productos locales
                    checkLocalProducts()
                }

                is SyncResult.Error -> {
                    _syncState.value = SyncState.Error(result.message)
                    _message.value = "Error de sincronización: ${result.message}"
                }

                SyncResult.NoConnection -> {
                    _syncState.value = SyncState.Error("Sin conexión")
                    _message.value = "No hay conexión a internet"
                }
            }

            // Resetear estado después de 3 segundos
            kotlinx.coroutines.delay(3000)
            _syncState.value = SyncState.Idle

        } catch (e: Exception) {
            _isLoading.value = false
            _syncState.value = SyncState.Error(e.message ?: "Error desconocido")
            _message.value = "Error: ${e.message}"
        }
    }

    // ==================== AGREGAR PRODUCTO ====================

    fun agregarProducto(p: ProductoEntity, onSuccess: () -> Unit = {}) = viewModelScope.launch {
        _isLoading.value = true

        try {
            val result = repo.insert(p)
            _isLoading.value = false

            result.onSuccess {
                _message.value = "Producto agregado exitosamente"
                checkLocalProducts() // Actualizar mapa
                onSuccess()
            }.onFailure { error ->
                _message.value = "Error: ${error.message}"
            }
        } catch (e: Exception) {
            _isLoading.value = false
            _message.value = "Error: ${e.message}"
        }
    }

    // ==================== ACTUALIZAR PRODUCTO ====================

    fun actualizarProducto(p: ProductoEntity, onSuccess: () -> Unit = {}) = viewModelScope.launch {
        _isLoading.value = true

        try {
            val result = repo.update(p)
            _isLoading.value = false

            result.onSuccess {
                _message.value = "Producto actualizado"
                onSuccess()
            }.onFailure { error ->
                _message.value = "Error: ${error.message}"
            }
        } catch (e: Exception) {
            _isLoading.value = false
            _message.value = "Error: ${e.message}"
        }
    }

    // ==================== ELIMINAR PRODUCTO ====================

    fun eliminarProducto(p: ProductoEntity) = viewModelScope.launch {
        _isLoading.value = true

        try {
            val result = repo.delete(p)
            _isLoading.value = false

            result.onSuccess {
                _message.value = "Producto eliminado"
                _productosLocales.value = _productosLocales.value - p.id
            }.onFailure { error ->
                _message.value = "Error: ${error.message}"
            }
        } catch (e: Exception) {
            _isLoading.value = false
            _message.value = "Error: ${e.message}"
        }
    }

    // ==================== CONSUMIR ====================

    fun consumirProducto(producto: ProductoEntity) = viewModelScope.launch {
        _isLoading.value = true

        try {
            val result = repo.consumirProducto(producto)
            _isLoading.value = false

            result.onSuccess {
                _message.value = "Stock consumido"
            }.onFailure { error ->
                _message.value = error.message
            }
        } catch (e: Exception) {
            _isLoading.value = false
            _message.value = "Error: ${e.message}"
        }
    }

    // ==================== REABASTECER ====================

    fun reabastecerProducto(producto: ProductoEntity) = viewModelScope.launch {
        _isLoading.value = true

        try {
            val result = repo.reabastecerProducto(producto)
            _isLoading.value = false

            result.onSuccess {
                _message.value = "Stock reabastecido"
            }.onFailure { error ->
                _message.value = error.message
            }
        } catch (e: Exception) {
            _isLoading.value = false
            _message.value = "Error: ${e.message}"
        }
    }

    // Limpiar mensajes
    fun clearMessage() {
        _message.value = null
    }
}

// ==================== ESTADOS DE SINCRONIZACIÓN ====================

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Success(
        val uploaded: Int,
        val downloaded: Int,
        val updated: Int
    ) : SyncState()
    data class Error(val message: String) : SyncState()
}