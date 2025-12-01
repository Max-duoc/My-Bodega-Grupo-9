package com.example.mybodega_grupo9.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAll(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    suspend fun getAllList(): List<ProductoEntity>

    @Query("UPDATE productos SET cantidad = :nuevaCantidad WHERE id = :id")
    suspend fun updateCantidad(id: Int, nuevaCantidad: Int)

    // 🔥 CAMBIO: Ahora devuelve el ID insertado
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: ProductoEntity): Long

    @Delete
    suspend fun delete(producto: ProductoEntity)

    @Update
    suspend fun update(producto: ProductoEntity)

    @Query("DELETE FROM productos")
    suspend fun deleteAll()
}