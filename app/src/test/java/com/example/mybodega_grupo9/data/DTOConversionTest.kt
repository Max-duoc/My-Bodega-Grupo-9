package com.example.mybodega_grupo9.data

import com.example.mybodega_grupo9.data.remote.ProductoCreateDTO
import com.example.mybodega_grupo9.data.remote.ProductoUpdateDTO
import com.example.mybodega_grupo9.data.remote.StockOperationDTO
import org.junit.Test
import org.junit.Assert.*

class DTOConversionTest {

    @Test
    fun `ProductoCreateDTO should be created correctly`() {
        // Arrange & Act
        val dto = ProductoCreateDTO(
            nombre = "Test Product",
            categoria = "Test",
            cantidad = 10,
            descripcion = "Description",
            ubicacion = "Location",
            imagenUrl = "url"
        )

        // Assert
        assertEquals("Test Product", dto.nombre)
        assertEquals("Test", dto.categoria)
        assertEquals(10, dto.cantidad)
        assertEquals("Description", dto.descripcion)
        assertEquals("Location", dto.ubicacion)
        assertEquals("url", dto.imagenUrl)
    }

    @Test
    fun `ProductoUpdateDTO should allow null values`() {
        // Arrange & Act
        val dto = ProductoUpdateDTO(
            nombre = "Updated Name"
        )

        // Assert
        assertEquals("Updated Name", dto.nombre)
        assertNull(dto.categoria)
        assertNull(dto.cantidad)
        assertNull(dto.descripcion)
    }

    @Test
    fun `StockOperationDTO should have default cantidad of 1`() {
        // Arrange & Act
        val dto = StockOperationDTO(productoId = 1)

        // Assert
        assertEquals(1L, dto.productoId)
        assertEquals(1, dto.cantidad)
    }

    @Test
    fun `StockOperationDTO should accept custom cantidad`() {
        // Arrange & Act
        val dto = StockOperationDTO(productoId = 1, cantidad = 5)

        // Assert
        assertEquals(5, dto.cantidad)
    }
}