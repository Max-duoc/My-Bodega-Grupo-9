package com.example.mybodega_grupo9.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mybodega_grupo9.model.UsuarioErrores
import com.example.mybodega_grupo9.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel: ViewModel() {
    // Estado interno mutable [cite: 100]
    private val _estado = MutableStateFlow(UsuarioUiState())
    // Estado expuesto para la UI [cite: 103, 104]
    val estado: StateFlow<UsuarioUiState> = _estado.asStateFlow()

    // Funciones para actualizar cada campo y limpiar su error [cite: 105]
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    fun onAceptanTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }

    // Validación global del formulario
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
        correo = if (!estadoActual.correo.contains("@")) "Correo inválido" else null,
        clave = if (estadoActual.clave.length < 6) "Debe tener al menos 6 caracteres" else null,
        direccion = if (estadoActual.direccion.isBlank()) "Campo obligatorio" else null
        )

        _estado.update { it.copy(errores = errores) }

        // Retorna 'true' si hay errores, 'false' si no los hay.
        // La guía lo hace al revés, pero es más semántico así.
        // Ajusta la lógica en el botón si sigues la guía al pie de la letra.
        return errores.nombre == null && errores.correo == null && errores.clave == null && errores.direccion == null
    }
}