package com.example.mybodega_grupo9.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mybodega_grupo9.data.ProductoRepository
import com.example.mybodega_grupo9.data.local.ProductoEntity
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class ProductoViewModelTest {

    // Regla para LiveData/StateFlow síncronos
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ProductoViewModel
    private lateinit var mockRepository: ProductoRepository
    private lateinit var mockApplication: Application

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockApplication = mockk(relaxed = true)
        mockRepository = mockk(relaxed = true)

        // Mock del constructor de ProductoRepository
        mockkConstructor(ProductoRepository::class)
        every { anyConstructed<ProductoRepository>().getAll() } returns flowOf(emptyList())

        viewModel = ProductoViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `agregarProducto should call repository insert and emit success message`() = runTest {
        // Arrange
        val producto = ProductoEntity(
            id = 0,
            nombre = "Test Product",
            categoria = "Test",
            cantidad = 10,
            descripcion = "Test description",
            ubicacion = "Test location"
        )

        coEvery { anyConstructed<ProductoRepository>().insert(any()) } returns Result.success(producto)

        var successCalled = false

        // Act
        viewModel.agregarProducto(producto) {
            successCalled = true
        }

        advanceUntilIdle()

        // Assert
        assertTrue("Success callback should be called", successCalled)
        assertEquals("Producto agregado exitosamente", viewModel.message.value)
        coVerify(exactly = 1) { anyConstructed<ProductoRepository>().insert(producto) }
    }

    @Test
    fun `agregarProducto should emit error message on failure`() = runTest {
        // Arrange
        val producto = ProductoEntity(
            id = 0,
            nombre = "Test Product",
            categoria = "Test",
            cantidad = 10,
            descripcion = null,
            ubicacion = null
        )

        val errorMessage = "Network error"
        coEvery { anyConstructed<ProductoRepository>().insert(any()) } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.agregarProducto(producto)
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.message.value?.contains("Error") == true)
        coVerify(exactly = 1) { anyConstructed<ProductoRepository>().insert(producto) }
    }

    @Test
    fun `actualizarProducto should call repository update`() = runTest {
        // Arrange
        val producto = ProductoEntity(
            id = 1,
            nombre = "Updated Product",
            categoria = "Test",
            cantidad = 20,
            descripcion = null,
            ubicacion = null
        )

        coEvery { anyConstructed<ProductoRepository>().update(any()) } returns Result.success(producto)

        // Act
        viewModel.actualizarProducto(producto)
        advanceUntilIdle()

        // Assert
        assertEquals("Producto actualizado", viewModel.message.value)
        coVerify(exactly = 1) { anyConstructed<ProductoRepository>().update(producto) }
    }

    @Test
    fun `eliminarProducto should call repository delete`() = runTest {
        // Arrange
        val producto = ProductoEntity(
            id = 1,
            nombre = "Product to delete",
            categoria = "Test",
            cantidad = 5,
            descripcion = null,
            ubicacion = null
        )

        coEvery { anyConstructed<ProductoRepository>().delete(any()) } returns Result.success(Unit)

        // Act
        viewModel.eliminarProducto(producto)
        advanceUntilIdle()

        // Assert
        assertEquals("Producto eliminado", viewModel.message.value)
        coVerify(exactly = 1) { anyConstructed<ProductoRepository>().delete(producto) }
    }

    @Test
    fun `consumirProducto should call repository consumir when stock available`() = runTest {
        // Arrange
        val producto = ProductoEntity(
            id = 1,
            nombre = "Product",
            categoria = "Test",
            cantidad = 5,
            descripcion = null,
            ubicacion = null
        )

        val updatedProduct = producto.copy(cantidad = 4)
        coEvery { anyConstructed<ProductoRepository>().consumirProducto(any()) } returns Result.success(updatedProduct)

        // Act
        viewModel.consumirProducto(producto)
        advanceUntilIdle()

        // Assert
        assertEquals("Stock consumido", viewModel.message.value)
        coVerify(exactly = 1) { anyConstructed<ProductoRepository>().consumirProducto(producto) }
    }

    @Test
    fun `reabastecerProducto should call repository reabastecer`() = runTest {
        // Arrange
        val producto = ProductoEntity(
            id = 1,
            nombre = "Product",
            categoria = "Test",
            cantidad = 5,
            descripcion = null,
            ubicacion = null
        )

        val updatedProduct = producto.copy(cantidad = 6)
        coEvery { anyConstructed<ProductoRepository>().reabastecerProducto(any()) } returns Result.success(updatedProduct)

        // Act
        viewModel.reabastecerProducto(producto)
        advanceUntilIdle()

        // Assert
        assertEquals("Stock reabastecido", viewModel.message.value)
        coVerify(exactly = 1) { anyConstructed<ProductoRepository>().reabastecerProducto(producto) }
    }

    @Test
    fun `clearMessage should set message to null`() {
        // Act
        viewModel.clearMessage()

        // Assert
        assertNull(viewModel.message.value)
    }

    @Test
    fun `isLoading should be true during operation`() = runTest {
        // Arrange
        val producto = ProductoEntity(
            id = 0,
            nombre = "Test",
            categoria = "Test",
            cantidad = 1,
            descripcion = null,
            ubicacion = null
        )

        coEvery { anyConstructed<ProductoRepository>().insert(any()) } coAnswers {
            kotlinx.coroutines.delay(100)
            Result.success(producto)
        }

        // Act
        viewModel.agregarProducto(producto)

        // Assert - Should be loading
        assertTrue(viewModel.isLoading.value)

        advanceUntilIdle()

        // After operation completes
        assertFalse(viewModel.isLoading.value)
    }
}