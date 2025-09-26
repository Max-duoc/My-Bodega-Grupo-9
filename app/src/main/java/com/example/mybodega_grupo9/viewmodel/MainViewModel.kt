package com.example.mybodega_grupo9.viewmodel


import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mybodega_grupo9.model.Item


class MainViewModel : ViewModel() {
    private val _items = mutableStateListOf<Item>()
    val items: List<Item> get() = _items

    init {
        // Datos de prueba
        _items.add(Item(1, "Arroz", "Comida", 2, "Cocina"))
        _items.add(Item(2, "Detergente", "Limpieza", 1, "Lavadero"))
    }

    fun agregarItem(item: Item) {
        _items.add(item)
    }
}
