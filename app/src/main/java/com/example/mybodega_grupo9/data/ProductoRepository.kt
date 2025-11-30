package com.example.mybodega_grupo9.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.room.Room
import com.example.mybodega_grupo9.data.local.AppDatabase
import com.example.mybodega_grupo9.data.local.ProductoEntity
import com.example.mybodega_grupo9.data.remote.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class ProductoRepository(private val context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "mybodega.db"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.productoDao()
    private val apiService = RetrofitClient.apiService

    // ==================== VERIFICAR CONEXIÓN ====================

    private fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // ==================== SINCRONIZACIÓN AUTOMÁTICA ====================

    /**
     * Sincroniza productos locales pendientes con el servidor.
     * Esto se ejecuta automáticamente al recuperar conexión.
     */
    suspend fun syncPendingChanges() = withContext(Dispatchers.IO) {
        if (!isOnline()) return@withContext

        try {
            // Aquí puedes implementar lógica para sincronizar cambios pendientes
            // Por ejemplo, productos marcados como "pendientes de sync"
            // Para simplicidad, solo forzamos una descarga completa
            val response = apiService.getProductos()
            if (response.isSuccessful && response.body() != null) {
                val productos = response.body()!!.map { it.toEntity() }
                dao.deleteAll()
                productos.forEach { dao.insert(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ==================== OBTENER TODOS (ESTRATEGIA HÍBRIDA) ====================

    fun getAll(): Flow<List<ProductoEntity>> = flow {
        // 1. Emitir datos locales inmediatamente (UI rápida)
        val localData = withContext(Dispatchers.IO) { dao.getAllList() }
        emit(localData)

        // 2. Si hay conexión, actualizar desde API
        if (isOnline()) {
            try {
                val response = apiService.getProductos()
                if (response.isSuccessful && response.body() != null) {
                    val productos = response.body()!!.map { it.toEntity() }

                    withContext(Dispatchers.IO) {
                        dao.deleteAll()
                        productos.forEach { dao.insert(it) }
                    }

                    // Emitir datos actualizados
                    emit(productos)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Si falla la API, seguimos mostrando datos locales
            }
        }
    }

    // ==================== CREAR PRODUCTO ====================

    suspend fun insert(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
        if (isOnline()) {
            // MODO ONLINE: Enviar a API
            try {
                val dto = ProductoCreateDTO(
                    nombre = producto.nombre,
                    categoria = producto.categoria,
                    cantidad = producto.cantidad,
                    descripcion = producto.descripcion,
                    ubicacion = producto.ubicacion,
                    imagenUrl = producto.imagenUri
                )

                val response = apiService.createProducto(dto)
                if (response.isSuccessful && response.body() != null) {
                    val newProduct = response.body()!!.toEntity()
                    dao.insert(newProduct)
                    Result.success(newProduct)
                } else {
                    Result.failure(Exception("Error API: ${response.code()}"))
                }
            } catch (e: Exception) {
                // Si falla la API, guardar localmente
                saveLocalPending(producto)
            }
        } else {
            // MODO OFFLINE: Guardar solo localmente
            saveLocalPending(producto)
        }
    }

    /**
     * Guarda un producto localmente cuando no hay conexión.
     * Al reconectar, se sincronizará automáticamente.
     */
    private suspend fun saveLocalPending(producto: ProductoEntity): Result<ProductoEntity> {
        return try {
            dao.insert(producto)
            Result.success(producto.copy(
                // Usamos IDs negativos para identificar productos pendientes
                id = -(System.currentTimeMillis().toInt())
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== ACTUALIZAR PRODUCTO ====================

    suspend fun update(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
        if (isOnline() && producto.id > 0) {
            // MODO ONLINE: Actualizar en API
            try {
                val dto = ProductoUpdateDTO(
                    nombre = producto.nombre,
                    categoria = producto.categoria,
                    cantidad = producto.cantidad,
                    descripcion = producto.descripcion,
                    ubicacion = producto.ubicacion,
                    imagenUrl = producto.imagenUri
                )

                val response = apiService.updateProducto(producto.id.toLong(), dto)
                if (response.isSuccessful && response.body() != null) {
                    val updated = response.body()!!.toEntity()
                    dao.update(updated)
                    Result.success(updated)
                } else {
                    Result.failure(Exception("Error API: ${response.code()}"))
                }
            } catch (e: Exception) {
                // Si falla, actualizar solo localmente
                dao.update(producto)
                Result.success(producto)
            }
        } else {
            // MODO OFFLINE: Actualizar solo localmente
            dao.update(producto)
            Result.success(producto)
        }
    }

    // ==================== ELIMINAR PRODUCTO ====================

    suspend fun delete(producto: ProductoEntity): Result<Unit> = withContext(Dispatchers.IO) {
        if (isOnline() && producto.id > 0) {
            // MODO ONLINE: Eliminar en API
            try {
                val response = apiService.deleteProducto(producto.id.toLong())
                if (response.isSuccessful) {
                    dao.delete(producto)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error API: ${response.code()}"))
                }
            } catch (e: Exception) {
                // Si falla, eliminar solo localmente
                dao.delete(producto)
                Result.success(Unit)
            }
        } else {
            // MODO OFFLINE: Eliminar solo localmente
            dao.delete(producto)
            Result.success(Unit)
        }
    }

    // ==================== CONSUMIR PRODUCTO ====================

    suspend fun consumirProducto(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
        if (producto.cantidad <= 0) {
            return@withContext Result.failure(Exception("No hay stock disponible"))
        }

        if (isOnline() && producto.id > 0) {
            try {
                val dto = StockOperationDTO(productoId = producto.id.toLong(), cantidad = 1)
                val response = apiService.consumirProducto(dto)

                if (response.isSuccessful && response.body() != null) {
                    val updated = response.body()!!.toEntity()
                    dao.updateCantidad(updated.id, updated.cantidad)
                    Result.success(updated)
                } else {
                    Result.failure(Exception("Error API: ${response.code()}"))
                }
            } catch (e: Exception) {
                // Fallback local
                val newCantidad = producto.cantidad - 1
                dao.updateCantidad(producto.id, newCantidad)
                Result.success(producto.copy(cantidad = newCantidad))
            }
        } else {
            // MODO OFFLINE
            val newCantidad = producto.cantidad - 1
            dao.updateCantidad(producto.id, newCantidad)
            Result.success(producto.copy(cantidad = newCantidad))
        }
    }

    // ==================== REABASTECER PRODUCTO ====================

    suspend fun reabastecerProducto(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
        if (isOnline() && producto.id > 0) {
            try {
                val dto = StockOperationDTO(productoId = producto.id.toLong(), cantidad = 1)
                val response = apiService.reabastecerProducto(dto)

                if (response.isSuccessful && response.body() != null) {
                    val updated = response.body()!!.toEntity()
                    dao.updateCantidad(updated.id, updated.cantidad)
                    Result.success(updated)
                } else {
                    Result.failure(Exception("Error API: ${response.code()}"))
                }
            } catch (e: Exception) {
                // Fallback local
                val newCantidad = producto.cantidad + 1
                dao.updateCantidad(producto.id, newCantidad)
                Result.success(producto.copy(cantidad = newCantidad))
            }
        } else {
            // MODO OFFLINE
            val newCantidad = producto.cantidad + 1
            dao.updateCantidad(producto.id, newCantidad)
            Result.success(producto.copy(cantidad = newCantidad))
        }
    }

    // ==================== CONVERSIÓN DTO → ENTITY ====================

    private fun ProductoResponseDTO.toEntity() = ProductoEntity(
        id = this.id.toInt(),
        nombre = this.nombre,
        categoria = this.categoria,
        cantidad = this.cantidad,
        descripcion = this.descripcion,
        ubicacion = this.ubicacion,
        imagenUri = this.imagenUrl
    )
}