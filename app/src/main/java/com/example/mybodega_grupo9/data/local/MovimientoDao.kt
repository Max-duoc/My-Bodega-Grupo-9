package com.example.mybodega_grupo9.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    fun getAll(): Flow<List<MovimientoEntity>>

    // ⬇️ AGREGAR ESTE MÉTODO:
    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    suspend fun getAllList(): List<MovimientoEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movimiento: MovimientoEntity)

    @Query("DELETE FROM movimientos")
    suspend fun clearAll()
}
