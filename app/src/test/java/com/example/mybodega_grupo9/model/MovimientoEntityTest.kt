package com.example.mybodega_grupo9.model

import com.example.mybodega_grupo9.data.local.MovimientoEntity
import org.junit.Test
import org.junit.Assert.*

class MovimientoEntityTest {

    @Test
    fun `MovimientoEntity should be created with valid data`() {
        // Arrange
        val currentTime = System.currentTimeMillis()

        // Act
        val movimiento = MovimientoEntity(
            id = 1,
            tipo = "Agregar",
            producto = "Test Product",
            fecha = currentTime
        )

        // Assert
        assertEquals(1, movimiento.id)
        assertEquals("Agregar", movimiento.tipo)
        assertEquals("Test Product", movimiento.producto)
        assertEquals(currentTime, movimiento.fecha)
    }

    @Test
    fun `MovimientoEntity should support different tipos`() {
        // Arrange & Act
        val tipos = listOf("Agregar", "Editar", "Eliminar", "Consumo", "Reabastecimiento")
        val movimientos = tipos.map { tipo ->
            MovimientoEntity(
                id = 0,
                tipo = tipo,
                producto = "Product",
                fecha = System.currentTimeMillis()
            )
        }

        // Assert
        assertEquals(5, movimientos.size)
        assertEquals("Agregar", movimientos[0].tipo)
        assertEquals("Consumo", movimientos[3].tipo)
    }

    @Test
    fun `MovimientoEntity copy should create new instance`() {
        // Arrange
        val original = MovimientoEntity(
            id = 1,
            tipo = "Agregar",
            producto = "Original Product",
            fecha = System.currentTimeMillis()
        )

        // Act
        val updated = original.copy(producto = "Updated Product")

        // Assert
        assertEquals("Updated Product", updated.producto)
        assertEquals(original.tipo, updated.tipo)
        assertEquals(original.id, updated.id)
    }
}