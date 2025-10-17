package com.example.mybodega_grupo9.data

import android.content.Context
import androidx.room.Room
import com.example.mybodega_grupo9.data.local.AppDatabase
import com.example.mybodega_grupo9.data.local.ProductoEntity
import kotlinx.coroutines.flow.Flow

class ProductoRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "mybodega.db"
    ).build()

    private val dao = db.productoDao()

    fun getAll(): Flow<List<ProductoEntity>> = dao.getAll()

    suspend fun insert(producto: ProductoEntity) = dao.insert(producto)
    suspend fun delete(producto: ProductoEntity) = dao.delete(producto)

    suspend fun update(producto: ProductoEntity) = dao.update(producto)

}
