package com.example.mybodega_grupo9.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybodega_grupo9.data.ProductoRepository
import com.example.mybodega_grupo9.data.local.ProductoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductoViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ProductoRepository(app)


    val productos = repo.getAll().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun agregarProducto(p: ProductoEntity) = viewModelScope.launch {
        repo.insert(p)
    }

    fun eliminarProducto(p: ProductoEntity) = viewModelScope.launch {

        repo.delete(p)
    }

    fun actualizarProducto(p: ProductoEntity) = viewModelScope.launch {
        repo.update(p)
    }

}

