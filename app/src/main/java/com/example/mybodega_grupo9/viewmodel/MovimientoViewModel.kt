package com.example.mybodega_grupo9.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybodega_grupo9.data.MovimientoRepository
import com.example.mybodega_grupo9.data.local.MovimientoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovimientoViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MovimientoRepository(app)

    // ✅ Cambiamos a WhileSubscribed para mantener actualizado el flujo mientras se observa
    val movimientos: StateFlow<List<MovimientoEntity>> = repo.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // se mantiene vivo 5s después de que nadie lo observa
            initialValue = emptyList()
        )

    fun registrarMovimiento(tipo: String, productoNombre: String) {
        viewModelScope.launch {
            val mov = MovimientoEntity(tipo = tipo, producto = productoNombre)
            repo.insert(mov)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repo.clearAll()
        }
    }
}


