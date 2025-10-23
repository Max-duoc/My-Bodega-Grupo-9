package com.example.mybodega_grupo9.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipo: String,        // "Agregar", "Editar", "Eliminar"
    val producto: String,    // nombre del producto (o id+nombre si prefieres)
    val fecha: Long = System.currentTimeMillis()
)

