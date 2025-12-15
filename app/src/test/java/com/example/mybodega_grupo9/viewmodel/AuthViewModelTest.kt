package com.example.mybodega_grupo9.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mybodega_grupo9.data.AuthRepository
import com.example.mybodega_grupo9.data.remote.LoginResponseDTO
import com.example.mybodega_grupo9.data.remote.UsuarioResponseDTO
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AuthViewModel
    private lateinit var mockApplication: Application

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockApplication = mockk(relaxed = true)

        mockkConstructor(AuthRepository::class)

        viewModel = AuthViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `login with valid credentials should emit success message`() = runTest {
        // Arrange
        val usuario = UsuarioResponseDTO(
            id = 1,
            email = "test@test.com",
            nombre = "Test User",
            rol = "USUARIO",
            activo = true,
            fechaCreacion = LocalDateTime.now().toString(),
            ultimoAcceso = null
        )

        val loginResponse = LoginResponseDTO(
            success = true,
            message = "Login exitoso",
            usuario = usuario
        )

        coEvery {
            anyConstructed<AuthRepository>().login(any(), any())
        } returns Result.success(loginResponse)

        // Act
        viewModel.login("test@test.com", "password")
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.loginSuccess.value)
        assertTrue(viewModel.message.value?.contains("Bienvenido") == true)
    }

    @Test
    fun `login with invalid credentials should emit error message`() = runTest {
        // Arrange
        coEvery {
            anyConstructed<AuthRepository>().login(any(), any())
        } returns Result.failure(Exception("Credenciales incorrectas"))

        // Act
        viewModel.login("wrong@test.com", "wrongpass")
        advanceUntilIdle()

        // Assert
        assertFalse(viewModel.loginSuccess.value)
        assertTrue(viewModel.message.value?.contains("Error") == true)
    }

    @Test
    fun `register should call repository and emit success`() = runTest {
        // Arrange
        val usuario = UsuarioResponseDTO(
            id = 1,
            email = "new@test.com",
            nombre = "New User",
            rol = "USUARIO",
            activo = true,
            fechaCreacion = LocalDateTime.now().toString(),
            ultimoAcceso = null
        )

        coEvery {
            anyConstructed<AuthRepository>().register(any(), any(), any(), any())
        } returns Result.success(usuario)

        var successCalled = false

        // Act
        viewModel.register("new@test.com", "password", "New User") {
            successCalled = true
        }
        advanceUntilIdle()

        // Assert
        assertTrue(successCalled)
        assertEquals("Usuario registrado exitosamente", viewModel.message.value)
    }

    @Test
    fun `logout should clear user data`() = runTest {
        // Arrange
        coEvery { anyConstructed<AuthRepository>().logout() } just Runs

        var successCalled = false

        // Act
        viewModel.logout { successCalled = true }
        advanceUntilIdle()

        // Assert
        assertTrue(successCalled)
        assertEquals("Sesión cerrada", viewModel.message.value)
    }
}