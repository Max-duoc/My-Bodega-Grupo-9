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

    // ==================== VERIFICAR SI PRODUCTO EXISTE EN SERVIDOR ====================

    /**
     * Verifica si un producto con el mismo nombre ya existe en el servidor.
     * Esto evita duplicados al sincronizar.
     */
    suspend fun existsOnServer(nombre: String): Boolean = withContext(Dispatchers.IO) {
        if (!isOnline()) return@withContext false

        return@withContext try {
            val response = apiService.getProductos()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.any { it.nombre.equals(nombre, ignoreCase = true) }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ==================== SUBIR PRODUCTO INDIVIDUAL AL SERVIDOR ====================

    /**
     * Sube un producto específico al servidor de manera manual.
     * Se usa cuando el usuario presiona el botón "Subir" en la card.
     */
    suspend fun uploadProductToServer(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
        if (!isOnline()) {
            return@withContext Result.failure(Exception("No hay conexión a internet"))
        }

        // Verificar si ya existe en el servidor
        if (existsOnServer(producto.nombre)) {
            return@withContext Result.failure(Exception("Ya existe un producto con este nombre en el servidor"))
        }

        return@withContext try {
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
                val serverProduct = response.body()!!.toEntity()

                // Eliminar el producto local temporal
                dao.delete(producto)

                // Insertar el producto con el ID real del servidor
                dao.insert(serverProduct)

                Result.success(serverProduct)
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error al subir el producto: ${e.message}"))
        }
    }

    // ==================== VERIFICAR SI ES PRODUCTO LOCAL ====================

    /**
     * Verifica si un producto es local (no sincronizado) basándose en:
     * 1. ID autogenerado por Room (> 1000000)
     * 2. No existe en el servidor con el mismo nombre
     */
    suspend fun isLocalProduct(producto: ProductoEntity): Boolean = withContext(Dispatchers.IO) {
        // Si tiene ID muy alto, es definitivamente local
        if (producto.id > 1000000) return@withContext true

        // Si no hay conexión, asumimos que es local si tiene ID bajo
        if (!isOnline()) return@withContext (producto.id <= 0)

        // Verificar en el servidor
        return@withContext !existsOnServer(producto.nombre)
    }

    // ==================== SINCRONIZACIÓN BIDIRECCIONAL (MANTENER) ====================

    suspend fun syncPendingChanges(): SyncResult = withContext(Dispatchers.IO) {
        if (!isOnline()) {
            return@withContext SyncResult.NoConnection
        }

        try {
            val productosLocales = dao.getAllList()
            var uploaded = 0
            var updated = 0
            var downloaded = 0

            // 🔥 PASO 1: SUBIR productos offline al servidor (ID > 1000000)
            val productosOffline = productosLocales.filter { it.id > 1000000 }

            productosOffline.forEach { productoLocal ->
                try {
                    // Verificar si ya existe en el servidor
                    if (!existsOnServer(productoLocal.nombre)) {
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

                            dao.delete(productoLocal)
                            dao.insert(productoServidor)

                            uploaded++
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // 🔥 PASO 2: DESCARGAR productos del servidor
            val response = apiService.getProductos()
            if (response.isSuccessful && response.body() != null) {
                val productosServidor = response.body()!!.map { it.toEntity() }

                val productosLocalesActualizados = dao.getAllList()

                productosServidor.forEach { productoServidor ->
                    val productoLocal = productosLocalesActualizados.find {
                        it.id == productoServidor.id
                    }

                    if (productoLocal != null) {
                        dao.update(productoServidor)
                        updated++
                    } else {
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
                        val serverProduct = response.body()!!.toEntity()
                        dao.insert(serverProduct)
                        Result.success(serverProduct)
                    } else {
                        guardarOffline(producto)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
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
            dao.update(producto)

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
            dao.delete(producto)

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
            val newCantidad = producto.cantidad - 1
            dao.updateCantidad(producto.id, newCantidad)
            val updatedProduct = producto.copy(cantidad = newCantidad)

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
            val newCantidad = producto.cantidad + 1
            dao.updateCantidad(producto.id, newCantidad)
            val updatedProduct = producto.copy(cantidad = newCantidad)

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