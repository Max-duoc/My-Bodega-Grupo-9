package com.example.mybodega_grupo9.data.remote

data class LoginRequestDTO(
    val email: String,
    val password: String
)

data class UsuarioCreateDTO(
    val email: String,
    val password: String,
    val nombre: String,
    val rol: String = "USUARIO"
)

data class UsuarioResponseDTO(
    val id: Long,
    val email: String,
    val nombre: String,
    val rol: String,
    val activo: Boolean,
    val fechaCreacion: String,
    val ultimoAcceso: String?
)

data class LoginResponseDTO(
    val success: Boolean,
    val message: String,
    val usuario: UsuarioResponseDTO? = null,
    val token: String? = null
)
