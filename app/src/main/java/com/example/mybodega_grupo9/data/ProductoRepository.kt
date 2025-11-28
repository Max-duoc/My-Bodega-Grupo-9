package com.example.mybodega_grupo9.data

import android.content.Context
import androidx.room.Room
import com.example.mybodega_grupo9.data.local.AppDatabase
import com.example.mybodega_grupo9.data.local.ProductoEntity
import com.example.mybodega_grupo9.data.remote.RetrofitClient
import com.example.mybodega_grupo9.data.remote.ProductoCreateDTO
import com.example.mybodega_grupo9.data.remote.ProductoUpdateDTO
import com.example.mybodega_grupo9.data.remote.StockOperationDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class ProductoRepository(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "mybodega.db"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.productoDao()
    private val apiService = RetrofitClient.apiService

    // ==================== OBTENER TODOS (API-FIRST) ====================

    fun getAll(): Flow<List<ProductoEntity>> = flow {
        try {
            // 1. Intentar con API
            val response = apiService.getProductos()
            if (response.isSuccessful && response.body() != null) {
                val productos = response.body()!!.map { it.toEntity() }

                // 2. Guardar en Room como caché
                withContext(Dispatchers.IO) {
                    dao.deleteAll()
                    productos.forEach { dao.insert(it) }
                }

                // 3. Emitir datos de API
                emit(productos)
            } else {
                // Si falla API, usar caché local
                val localData = withContext(Dispatchers.IO) { dao.getAllList() }
                emit(localData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Si hay error de red, usar caché
            val localData = withContext(Dispatchers.IO) { dao.getAllList() }
            emit(localData)
        }
    }

    // ==================== CREAR PRODUCTO (API) ====================

    suspend fun insert(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
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

                // Guardar en caché local
                dao.insert(newProduct)

                Result.success(newProduct)
            } else {
                Result.failure(Exception("Error al crear producto: ${response.code()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // ==================== ACTUALIZAR PRODUCTO (API) ====================

    suspend fun update(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
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

                // Actualizar caché local
                dao.update(updated)

                Result.success(updated)
            } else {
                Result.failure(Exception("Error al actualizar: ${response.code()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // ==================== ELIMINAR PRODUCTO (API) ====================

    suspend fun delete(producto: ProductoEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteProducto(producto.id.toLong())
            if (response.isSuccessful) {
                // Eliminar de caché local
                dao.delete(producto)

                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar: ${response.code()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // ==================== CONSUMIR PRODUCTO (API) ====================

    suspend fun consumirProducto(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
        if (producto.cantidad <= 0) {
            return@withContext Result.failure(Exception("No hay stock disponible"))
        }

        try {
            val dto = StockOperationDTO(productoId = producto.id.toLong(), cantidad = 1)
            val response = apiService.consumirProducto(dto)

            if (response.isSuccessful && response.body() != null) {
                val updated = response.body()!!.toEntity()

                // Actualizar caché
                dao.updateCantidad(updated.id, updated.cantidad)

                Result.success(updated)
            } else {
                Result.failure(Exception("Error al consumir: ${response.code()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // ==================== REABASTECER PRODUCTO (API) ====================

    suspend fun reabastecerProducto(producto: ProductoEntity): Result<ProductoEntity> = withContext(Dispatchers.IO) {
        try {
            val dto = StockOperationDTO(productoId = producto.id.toLong(), cantidad = 1)
            val response = apiService.reabastecerProducto(dto)

            if (response.isSuccessful && response.body() != null) {
                val updated = response.body()!!.toEntity()

                // Actualizar caché
                dao.updateCantidad(updated.id, updated.cantidad)

                Result.success(updated)
            } else {
                Result.failure(Exception("Error al reabastecer: ${response.code()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // ==================== CONVERSIÓN DTO → ENTITY ====================

    private fun com.example.mybodega_grupo9.data.remote.ProductoResponseDTO.toEntity() = ProductoEntity(
        id = this.id.toInt(),
        nombre = this.nombre,
        categoria = this.categoria,
        cantidad = this.cantidad,
        descripcion = this.descripcion,
        ubicacion = this.ubicacion,
        imagenUri = this.imagenUrl
    )
}