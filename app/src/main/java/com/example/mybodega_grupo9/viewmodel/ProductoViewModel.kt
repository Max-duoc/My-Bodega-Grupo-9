package com.example.mybodega_grupo9.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mybodega_grupo9.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductoViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    fun agregarProducto(producto: Producto) {
        _productos.value = _productos.value + producto
    }

    fun obtenerProductoPorId(id: Int): Producto? {
        return _productos.value.find { it.id == id }
    }
}
