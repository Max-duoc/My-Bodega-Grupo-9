package com.example.mybodega_grupo9.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mybodega_grupo9.data.local.ProductoDao
import com.example.mybodega_grupo9.data.local.ProductoEntity
import com.example.mybodega_grupo9.data.remote.ApiService
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
class ProductoRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: ProductoRepository
    private lateinit var mockContext: Context
    private lateinit var mockDao: ProductoDao
    private lateinit var mockApiService: ApiService

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockContext = mockk(relaxed = true)
        mockDao = mockk(relaxed = true)
        mockApiService = mockk(relaxed = true)

        // Mock Room database builder
        mockkStatic(androidx.room.Room::class)

        repository = ProductoRepository(mockContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `insert should save product locally when offline`() = runTest {
        // Arrange
        val producto = ProductoEntity(
            id = 0,
            nombre = "Test Product",
            categoria = "Test",
            cantidad = 10,
            descripcion = null,
            ubicacion = null
        )

        coEvery { mockDao.insert(any()) } returns 1L
        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns null

        // Act
        val result = repository.insert(producto)
        advanceUntilIdle()

        // Assert
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { mockDao.insert(any()) }
    }

    @Test
    fun `getAll should return flow of products`() = runTest {
        // Arrange
        val productos = listOf(
            ProductoEntity(1, "Product 1", "Cat1", 5, null, null),
            ProductoEntity(2, "Product 2", "Cat2", 10, null, null)
        )

        every { mockDao.getAll() } returns flowOf(productos)

        // Act
        val flow = repository.getAll()
        advanceUntilIdle()

        // Assert
        flow.collect { list ->
            assertEquals(2, list.size)
            assertEquals("Product 1", list[0].nombre)
        }
    }
}