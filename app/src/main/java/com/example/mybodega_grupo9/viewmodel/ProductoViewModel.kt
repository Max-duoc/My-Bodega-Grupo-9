package com.example.mybodega_grupo9.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mybodega_grupo9.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductoViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    var productoImagenUri by mutableStateOf<Uri?>(null)
        private set

    fun agregarProducto(producto: Producto) {
        // ✅ Agregar el producto con la imagen guardada
        val productoConImagen = producto.copy(
            imagenUri = productoImagenUri?.toString()
        )
        _productos.value = _productos.value + productoConImagen

        // ✅ Limpiar la imagen después de guardar
        productoImagenUri = null
    }

    fun obtenerProductoPorId(id: Int): Producto? {
        return _productos.value.find { it.id == id }
    }

    fun setProductoImagen(uri: Uri?) {
        productoImagenUri = uri
    }
}