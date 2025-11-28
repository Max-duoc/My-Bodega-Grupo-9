package com.example.mybodega_grupo9.model

import com.example.mybodega_grupo9.data.local.ProductoEntity
import org.junit.Test
import org.junit.Assert.*

class ProductoEntityTest {

    @Test
    fun `ProductoEntity should be created with valid data`() {
        // Arrange & Act
        val producto = ProductoEntity(
            id = 1,
            nombre = "Test Product",
            categoria = "Test Category",
            cantidad = 10,
            descripcion = "Test Description",
            ubicacion = "Test Location",
            imagenUri = "test_uri"
        )

        // Assert
        assertEquals(1, producto.id)
        assertEquals("Test Product", producto.nombre)
        assertEquals("Test Category", producto.categoria)
        assertEquals(10, producto.cantidad)
        assertEquals("Test Description", producto.descripcion)
        assertEquals("Test Location", producto.ubicacion)
        assertEquals("test_uri", producto.imagenUri)
    }

    @Test
    fun `ProductoEntity should allow nullable fields`() {
        // Arrange & Act
        val producto = ProductoEntity(
            id = 1,
            nombre = "Test Product",
            categoria = "Test",
            cantidad = 5,
            descripcion = null,
            ubicacion = null,
            imagenUri = null
        )

        // Assert
        assertNull(producto.descripcion)
        assertNull(producto.ubicacion)
        assertNull(producto.imagenUri)
    }

    @Test
    fun `ProductoEntity copy should create new instance with updated values`() {
        // Arrange
        val original = ProductoEntity(
            id = 1,
            nombre = "Original",
            categoria = "Category",
            cantidad = 10,
            descripcion = "Description",
            ubicacion = "Location"
        )

        // Act
        val updated = original.copy(
            nombre = "Updated",
            cantidad = 20
        )

        // Assert
        assertEquals("Updated", updated.nombre)
        assertEquals(20, updated.cantidad)
        assertEquals(original.categoria, updated.categoria)
        assertEquals(original.id, updated.id)
    }

    @Test
    fun `ProductoEntity with zero quantity should be valid`() {
        // Arrange & Act
        val producto = ProductoEntity(
            id = 1,
            nombre = "Out of Stock",
            categoria = "Test",
            cantidad = 0,
            descripcion = null,
            ubicacion = null
        )

        // Assert
        assertEquals(0, producto.cantidad)
    }
}