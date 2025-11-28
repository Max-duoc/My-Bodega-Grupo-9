package com.example.mybodega_grupo9.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mybodega_grupo9.data.MovimientoRepository
import com.example.mybodega_grupo9.data.local.MovimientoEntity
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
class MovimientoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MovimientoViewModel
    private lateinit var mockApplication: Application

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockApplication = mockk(relaxed = true)

        mockkConstructor(MovimientoRepository::class)
        every { anyConstructed<MovimientoRepository>().getAll() } returns flowOf(emptyList())

        viewModel = MovimientoViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `movimientos should emit data from repository`() = runTest {
        // Arrange
        val movimientos = listOf(
            MovimientoEntity(
                id = 1,
                tipo = "Agregar",
                producto = "Test Product",
                fecha = System.currentTimeMillis()
            ),
            MovimientoEntity(
                id = 2,
                tipo = "Editar",
                producto = "Another Product",
                fecha = System.currentTimeMillis()
            )
        )

        every { anyConstructed<MovimientoRepository>().getAll() } returns flowOf(movimientos)

        val newViewModel = MovimientoViewModel(mockApplication)
        advanceUntilIdle()

        // Assert
        assertEquals(2, newViewModel.movimientos.value.size)
        assertEquals("Agregar", newViewModel.movimientos.value[0].tipo)
        assertEquals("Test Product", newViewModel.movimientos.value[0].producto)
    }

    @Test
    fun `clearAll should call repository clearAll on success`() = runTest {
        // Arrange
        coEvery { anyConstructed<MovimientoRepository>().clearAll() } returns Result.success(Unit)

        // Act
        viewModel.clearAll()
        advanceUntilIdle()

        // Assert
        assertEquals("Historial limpiado", viewModel.message.value)
        coVerify(exactly = 1) { anyConstructed<MovimientoRepository>().clearAll() }
    }

    @Test
    fun `clearAll should emit error message on failure`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        coEvery { anyConstructed<MovimientoRepository>().clearAll() } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.clearAll()
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.message.value?.contains("Error") == true)
        coVerify(exactly = 1) { anyConstructed<MovimientoRepository>().clearAll() }
    }

    @Test
    fun `clearMessage should set message to null`() {
        // Act
        viewModel.clearMessage()

        // Assert
        assertNull(viewModel.message.value)
    }
}