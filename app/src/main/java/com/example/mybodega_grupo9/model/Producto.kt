package com.example.mybodega_grupo9.model

data class Producto(
    val id: Int = 0,
    val nombre: String = "",
    val categoria: String = "",
    val cantidad: Int = 0,
    val descripcion: String = "",
    val ubicacion: String? = null // campo opcional
)

