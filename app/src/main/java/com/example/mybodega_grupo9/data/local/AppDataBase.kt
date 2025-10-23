package com.example.mybodega_grupo9.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProductoEntity::class, MovimientoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun movimientoDao(): MovimientoDao
}


