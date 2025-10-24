package com.example.mybodega_grupo9.data

import android.content.Context
import androidx.room.Room
import com.example.mybodega_grupo9.data.local.AppDatabase
import com.example.mybodega_grupo9.data.local.MovimientoEntity
import com.example.mybodega_grupo9.data.local.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ProductoRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "mybodega.db"
    ).build()

    private val dao = db.productoDao()
    private val movimientoDao = db.movimientoDao()

    fun getAll(): Flow<List<ProductoEntity>> = dao.getAll()

    suspend fun insert(producto: ProductoEntity) = dao.insert(producto)
    suspend fun delete(producto: ProductoEntity) = dao.delete(producto)

    suspend fun update(producto: ProductoEntity) = dao.update(producto)

    suspend fun consumirProducto(producto: ProductoEntity) = withContext(Dispatchers.IO) {
        if (producto.cantidad > 0) {
            val nuevaCantidad = producto.cantidad - 1
            dao.updateCantidad(producto.id, nuevaCantidad)
            movimientoDao.insert(
                MovimientoEntity(
                    tipo = "Consumo",
                    producto = producto.nombre
                )
            )
        }
    }

    // ✅ Nuevo: reabastecer producto (sumar cantidad)
    suspend fun reabastecerProducto(producto: ProductoEntity) = withContext(Dispatchers.IO) {
        val nuevaCantidad = producto.cantidad + 1
        dao.updateCantidad(producto.id, nuevaCantidad)
        movimientoDao.insert(
            MovimientoEntity(
                tipo = "Reabastecimiento",
                producto = producto.nombre
            )
        )
    }





}
