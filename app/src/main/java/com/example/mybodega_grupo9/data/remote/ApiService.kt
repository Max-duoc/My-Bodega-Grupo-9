package com.example.mybodega_grupo9.data.remote

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/productos")
    suspend fun getProductos(): Response<List<ProductoResponseDTO>>

    @POST("api/productos")
    suspend fun createProducto(@Body producto: ProductoCreateDTO): Response<ProductoResponseDTO>

    @PUT("api/productos/{id}")
    suspend fun updateProducto(
        @Path("id") id: Long,
        @Body producto: ProductoUpdateDTO
    ): Response<ProductoResponseDTO>

    @DELETE("api/productos/{id}")
    suspend fun deleteProducto(@Path("id") id: Long): Response<Unit>

    @POST("api/productos/consumir")
    suspend fun consumirProducto(@Body operation: StockOperationDTO): Response<ProductoResponseDTO>

    @POST("api/productos/reabastecer")
    suspend fun reabastecerProducto(@Body operation: StockOperationDTO): Response<ProductoResponseDTO>

    @GET("api/movimientos/recientes")
    suspend fun getMovimientosRecientes(): Response<List<MovimientoResponseDTO>>

    @DELETE("api/movimientos/limpiar")
    suspend fun clearMovimientos(): Response<Unit>
}