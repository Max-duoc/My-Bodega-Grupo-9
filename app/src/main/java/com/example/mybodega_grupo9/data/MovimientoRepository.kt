package com.example.mybodega_grupo9.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.room.Room
import com.example.mybodega_grupo9.data.local.AppDatabase
import com.example.mybodega_grupo9.data.local.MovimientoEntity
import com.example.mybodega_grupo9.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MovimientoRepository(private val context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "mybodega.db"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.movimientoDao()
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

    // ==================== SINCRONIZACIÓN AUTOMÁTICA ====================

    suspend fun syncPendingChanges() = withContext(Dispatchers.IO) {
        if (!isOnline()) return@withContext

        try {
            val response = apiService.getMovimientosRecientes()
            if (response.isSuccessful && response.body() != null) {
                val movimientos = response.body()!!.map { dto ->
                    MovimientoEntity(
                        id = dto.id.toInt(),
                        tipo = dto.tipo,
                        producto = dto.producto,
                        fecha = parseDateTime(dto.fecha)
                    )
                }

                // Reemplazar caché local con datos del servidor
                dao.clearAll()
                movimientos.forEach { dao.insert(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ==================== OBTENER MOVIMIENTOS (ESTRATEGIA HÍBRIDA) ====================

    fun getAll(): Flow<List<MovimientoEntity>> = flow {
        // 1. Emitir datos locales inmediatamente (UI rápida)
        val localData = withContext(Dispatchers.IO) { dao.getAllList() }
        emit(localData)

        // 2. Si hay conexión, actualizar desde API
        if (isOnline()) {
            try {
                val response = apiService.getMovimientosRecientes()
                if (response.isSuccessful && response.body() != null) {
                    val movimientos = response.body()!!.map { dto ->
                        MovimientoEntity(
                            id = dto.id.toInt(),
                            tipo = dto.tipo,
                            producto = dto.producto,
                            fecha = parseDateTime(dto.fecha)
                        )
                    }

                    withContext(Dispatchers.IO) {
                        dao.clearAll()
                        movimientos.forEach { dao.insert(it) }
                    }

                    // Emitir datos actualizados
                    emit(movimientos)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Si falla la API, seguimos mostrando datos locales
            }
        }
    }

    // ==================== LIMPIAR HISTORIAL (CORREGIDO) ====================

    suspend fun clearAll(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // 🔥 SIEMPRE LIMPIAR LOCALMENTE PRIMERO
            dao.clearAll()

            // Intentar limpiar en API si hay conexión
            if (isOnline()) {
                try {
                    apiService.clearMovimientos()
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Ya limpiamos localmente, no importa si falla la API
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error al limpiar movimientos: ${e.message}"))
        }
    }

    // ==================== PARSEAR FECHA ====================

    private fun parseDateTime(dateString: String): Long {
        return try {
            // Si el backend envía timestamp en milisegundos, úsalo directamente
            // Si envía formato ISO 8601, necesitas parsearlo
            // Por ahora, usamos la fecha actual como fallback
            System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}