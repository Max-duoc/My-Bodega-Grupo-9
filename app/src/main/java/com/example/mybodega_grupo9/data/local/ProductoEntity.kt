package com.example.mybodega_grupo9.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val categoria: String,
    val cantidad: Int,
    val descripcion: String?,
    val ubicacion: String?,
    val imagenUri: String? = null
)