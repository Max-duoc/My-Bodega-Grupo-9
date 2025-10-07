package com.example.mybodega_grupo9.viewmodel


import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mybodega_grupo9.model.Producto


class MainViewModel : ViewModel() {
    private val _productos = mutableStateListOf<Producto>()
    val productos: List<Producto> get() = _productos

    init {
        // Datos de prueba
        _productos.add(Producto(1, "Arroz", "Comida", 2, "Cocina"))
        _productos.add(Producto(2, "Detergente", "Limpieza", 1, "Lavadero"))
    }

    fun agregarItem(producto: Producto) {
        _productos.add(producto)
    }
}
