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
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }

    // ==================== SINCRONIZACIÓN BIDIRECCIONAL ====================

    /**
     * Sincronización completa:
     * 1. Sube productos offline (ID negativos) al servidor
     * 2. Descarga productos del servidor
     * 3. Actualiza productos que existen en ambos lados
     */
    suspend fun syncPendingChanges(): SyncResult = withContext(Dispatchers.IO) {
        if (!isOnline()) {
            return@withContext SyncResult.NoConnection
        }

        try {
            val productosLocales = dao.getAllList()
            var uploaded = 0
            var updated = 0
            var downloaded = 0

            // 🔥 PASO 1: SUBIR productos offline al servidor (ID < 0 o ID muy alto)
            val productosOffline = productosLocales.filter {
                it.id <= 0 || it.id > 1000000 // IDs autogenerados por Room
            }

            productosOffline.forEach { productoLocal ->
                try {
                    val dto = ProductoCreateDTO(
                        nombre = productoLocal.nombre,
                        categoria = productoLocal.categoria,
                        cantidad = productoLocal.cantidad,
                        descripcion = productoLocal.descripcion,
                        ubicacion = productoLocal.ubicacion,
                        imagenUrl = productoLocal.imagenUri
                    )

                    val response = apiService.createProducto(dto)
                    if (response.isSuccessful && response.body() != null) {
                        val productoServidor = response.body()!!.toEntity()

                        // Eliminar el producto local temporal
                        dao.delete(productoLocal)

                        // Insertar el producto con el ID real del servidor
                        dao.insert(productoServidor)

                        uploaded++
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Continuar con el siguiente producto
                }
            }

            // 🔥 PASO 2: DESCARGAR productos del servidor
            val response = apiService.getProductos()
            if (response.isSuccessful && response.body() != null) {
                val productosServidor = response.body()!!.map { it.toEntity() }

                // Obtener productos locales actualizados (sin los offline que ya subimos)
                val productosLocalesActualizados = dao.getAllList()

                productosServidor.forEach { productoServidor ->
                    val productoLocal = productosLocalesActualizados.find {
                        it.id == productoServidor.id
                    }

                    if (productoLocal != null) {
                        // Producto existe localmente - actualizar
                        dao.update(productoServidor)
                        updated++
                    } else {
                        // Producto nuevo del servidor - descargar
                        dao.insert(productoServidor)
                        downloaded++
                    }
                }
            }

            return@withContext SyncResult.Success(
                uploaded = uploaded,
                downloaded = downloaded,
                updated = updated
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext SyncResult.Error(e.message ?: "Error desconocido")
        }
    }

    // ==================== OBTENER TODOS ====================

    fun getAll(): Flow<List<ProductoEntity>> {
        return dao.getAll()
    }

    // ==================== CREAR PRODUCTO ====================

    suspend fun insert(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (isOnline()) {
                // 🔥 MODO ONLINE: Crear en servidor primero
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
                        // Guardar con ID del servidor
                        val serverProduct = response.body()!!.toEntity()
                        dao.insert(serverProduct)
                        Result.success(serverProduct)
                    } else {
                        // API falló, guardar localmente
                        guardarOffline(producto)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // API no disponible, guardar localmente
                    guardarOffline(producto)
                }
            } else {
                // 🔥 MODO OFFLINE: Guardar localmente
                guardarOffline(producto)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error al guardar el producto: ${e.message}"))
        }
    }

    /**
     * Guarda un producto localmente con ID temporal negativo
     */
    private suspend fun guardarOffline(producto: ProductoEntity): Result<ProductoEntity> {
        return try {
            val localId = dao.insert(producto)
            Result.success(producto.copy(id = localId.toInt()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== ACTUALIZAR PRODUCTO ====================

    suspend fun update(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Actualizar localmente primero
            dao.update(producto)

            // Intentar sincronizar con API si es un producto del servidor
            if (isOnline() && producto.id > 0 && producto.id < 1000000) {
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
                        Result.success(producto)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.success(producto)
                }
            } else {
                Result.success(producto)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error al actualizar el producto: ${e.message}"))
        }
    }

    // ==================== ELIMINAR PRODUCTO ====================

    suspend fun delete(producto: ProductoEntity): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Eliminar localmente primero
            dao.delete(producto)

            // Intentar eliminar en API si es un producto del servidor
            if (isOnline() && producto.id > 0 && producto.id < 1000000) {
                try {
                    apiService.deleteProducto(producto.id.toLong())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error al eliminar el producto: ${e.message}"))
        }
    }

    // ==================== CONSUMIR PRODUCTO ====================

    suspend fun consumirProducto(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
        if (producto.cantidad <= 0) {
            return@withContext Result.failure(Exception("No hay stock disponible"))
        }

        return@withContext try {
            // Actualizar localmente primero
            val newCantidad = producto.cantidad - 1
            dao.updateCantidad(producto.id, newCantidad)
            val updatedProduct = producto.copy(cantidad = newCantidad)

            // Intentar sincronizar con API
            if (isOnline() && producto.id > 0 && producto.id < 1000000) {
                try {
                    val dto = StockOperationDTO(productoId = producto.id.toLong(), cantidad = 1)
                    val response = apiService.consumirProducto(dto)

                    if (response.isSuccessful && response.body() != null) {
                        val serverProduct = response.body()!!.toEntity()
                        dao.update(serverProduct)
                        Result.success(serverProduct)
                    } else {
                        Result.success(updatedProduct)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.success(updatedProduct)
                }
            } else {
                Result.success(updatedProduct)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error al consumir el producto: ${e.message}"))
        }
    }

    // ==================== REABASTECER PRODUCTO ====================

    suspend fun reabastecerProducto(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Actualizar localmente primero
            val newCantidad = producto.cantidad + 1
            dao.updateCantidad(producto.id, newCantidad)
            val updatedProduct = producto.copy(cantidad = newCantidad)

            // Intentar sincronizar con API
            if (isOnline() && producto.id > 0 && producto.id < 1000000) {
                try {
                    val dto = StockOperationDTO(productoId = producto.id.toLong(), cantidad = 1)
                    val response = apiService.reabastecerProducto(dto)

                    if (response.isSuccessful && response.body() != null) {
                        val serverProduct = response.body()!!.toEntity()
                        dao.update(serverProduct)
                        Result.success(serverProduct)
                    } else {
                        Result.success(updatedProduct)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.success(updatedProduct)
                }
            } else {
                Result.success(updatedProduct)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error al reabastecer el producto: ${e.message}"))
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

// ==================== RESULTADO DE SINCRONIZACIÓN ====================

sealed class SyncResult {
    data class Success(
        val uploaded: Int,
        val downloaded: Int,
        val updated: Int
    ) : SyncResult()

    data class Error(val message: String) : SyncResult()
    object NoConnection : SyncResult()
}