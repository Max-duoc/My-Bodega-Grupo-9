package com.example.mybodega_grupo9.data

import android.content.Context
import androidx.room.Room
import com.example.mybodega_grupo9.data.local.AppDatabase
import com.example.mybodega_grupo9.data.local.MovimientoDao
import com.example.mybodega_grupo9.data.local.MovimientoEntity
import kotlinx.coroutines.flow.Flow

class MovimientoRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "mybodega.db"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.movimientoDao()

    fun getAll(): Flow<List<MovimientoEntity>> = dao.getAll()

    suspend fun insert(m: MovimientoEntity) = dao.insert(m)

    suspend fun clearAll() = dao.clearAll()
}
