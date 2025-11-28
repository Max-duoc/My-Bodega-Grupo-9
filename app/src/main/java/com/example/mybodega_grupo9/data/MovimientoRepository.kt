package com.example.mybodega_grupo9.data

import android.content.Context
import androidx.room.Room
import com.example.mybodega_grupo9.data.local.AppDatabase
import com.example.mybodega_grupo9.data.local.MovimientoEntity
import com.example.mybodega_grupo9.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MovimientoRepository(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "mybodega.db"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.movimientoDao()
    private val apiService = RetrofitClient.apiService

    // ==================== OBTENER MOVIMIENTOS (API-FIRST) ====================

    fun getAll(): Flow<List<MovimientoEntity>> = flow {
        try {
            val response = apiService.getMovimientosRecientes()
            if (response.isSuccessful && response.body() != null) {
                val movimientos = response.body()!!.map { dto ->
                    MovimientoEntity(
                        id = dto.id.toInt(),
                        tipo = dto.tipo,
                        producto = dto.producto,
                        fecha = System.currentTimeMillis() // Simplificado
                    )
                }

                // Guardar en caché
                withContext(Dispatchers.IO) {
                    dao.clearAll()
                    movimientos.forEach { dao.insert(it) }
                }

                emit(movimientos)
            } else {
                // Fallback a caché local
                val localData = withContext(Dispatchers.IO) {
                    dao.getAllList()
                }
                emit(localData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Error de red, usar caché
            val localData = withContext(Dispatchers.IO) {
                dao.getAllList()
            }
            emit(localData)
        }
    }

    // ==================== LIMPIAR HISTORIAL (API) ====================

    suspend fun clearAll(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.clearMovimientos()
            if (response.isSuccessful) {
                dao.clearAll()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al limpiar: ${response.code()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // NOTA: Los movimientos se crean automáticamente en el backend
    // cuando haces operaciones CRUD de productos
}