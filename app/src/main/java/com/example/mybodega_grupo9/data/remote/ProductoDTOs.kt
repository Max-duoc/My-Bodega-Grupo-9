package com.example.mybodega_grupo9.data.remote

data class ProductoCreateDTO(
    val nombre: String,
    val categoria: String,
    val cantidad: Int,
    val descripcion: String? = null,
    val ubicacion: String? = null,
    val imagenUrl: String? = null
)

data class ProductoUpdateDTO(
    val nombre: String? = null,
    val categoria: String? = null,
    val cantidad: Int? = null,
    val descripcion: String? = null,
    val ubicacion: String? = null,
    val imagenUrl: String? = null
)

data class ProductoResponseDTO(
    val id: Long,
    val nombre: String,
    val categoria: String,
    val cantidad: Int,
    val descripcion: String?,
    val ubicacion: String?,
    val imagenUrl: String?,
    val fechaCreacion: String,
    val fechaActualizacion: String
)

data class StockOperationDTO(
    val productoId: Long,
    val cantidad: Int = 1
)

data class MovimientoResponseDTO(
    val id: Long,
    val tipo: String,
    val producto: String,
    val productoId: Long?,
    val cantidadAnterior: Int?,
    val cantidadNueva: Int?,
    val fecha: String,
    val detalles: String?
)