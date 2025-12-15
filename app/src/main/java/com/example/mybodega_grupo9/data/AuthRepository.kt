package com.example.mybodega_grupo9.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mybodega_grupo9.data.remote.LoginRequestDTO
import com.example.mybodega_grupo9.data.remote.LoginResponseDTO
import com.example.mybodega_grupo9.data.remote.RetrofitClient
import com.example.mybodega_grupo9.data.remote.UsuarioCreateDTO
import com.example.mybodega_grupo9.data.remote.UsuarioResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.authDataStore by preferencesDataStore("auth_prefs")

class AuthRepository(private val context: Context) {

    private val apiService = RetrofitClient.apiService

    companion object {
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_ROL_KEY = stringPreferencesKey("user_rol")
    }

    // ==================== LOGIN ====================
    suspend fun login(email: String, password: String): Result<LoginResponseDTO> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = apiService.login(LoginRequestDTO(email, password))

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    if (loginResponse.success && loginResponse.usuario != null) {
                        // Guardar datos del usuario en DataStore
                        saveUserData(loginResponse.usuario)
                        Result.success(loginResponse)
                    } else {
                        Result.failure(Exception(loginResponse.message))
                    }
                } else {
                    Result.failure(Exception("Error del servidor: ${response.code()}"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }

    // ==================== REGISTRO ====================
    suspend fun register(
        email: String,
        password: String,
        nombre: String,
        rol: String = "USUARIO"
    ): Result<UsuarioResponseDTO> = withContext(Dispatchers.IO) {
        return@withContext try {
            val dto = UsuarioCreateDTO(email, password, nombre, rol)
            val response = apiService.register(dto)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    // ==================== GUARDAR DATOS DEL USUARIO ====================
    private suspend fun saveUserData(usuario: UsuarioResponseDTO) {
        context.authDataStore.edit { prefs ->
            prefs[USER_ID_KEY] = usuario.id
            prefs[USER_EMAIL_KEY] = usuario.email
            prefs[USER_NAME_KEY] = usuario.nombre
            prefs[USER_ROL_KEY] = usuario.rol
        }
    }

    // ==================== OBTENER DATOS DEL USUARIO ====================
    fun getUserData(): Flow<UserData?> {
        return context.authDataStore.data.map { prefs ->
            val userId = prefs[USER_ID_KEY]
            if (userId != null) {
                UserData(
                    id = userId,
                    email = prefs[USER_EMAIL_KEY] ?: "",
                    nombre = prefs[USER_NAME_KEY] ?: "",
                    rol = prefs[USER_ROL_KEY] ?: "USUARIO"
                )
            } else {
                null
            }
        }
    }

    // ==================== LOGOUT ====================
    suspend fun logout() {
        context.authDataStore.edit { it.clear() }
    }
}

// Data class para los datos del usuario
data class UserData(
    val id: Long,
    val email: String,
    val nombre: String,
    val rol: String
)