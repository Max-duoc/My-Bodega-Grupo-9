package com.example.mybodega_grupo9.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybodega_grupo9.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ModeViewModel(app: Application) : AndroidViewModel(app) {

    private val _enabled = MutableStateFlow<Boolean?>(null)
    val enabled: StateFlow<Boolean?> = _enabled

    init {
        // Leer el valor almacenado al iniciar la app
        viewModelScope.launch {
            PreferencesManager.getModoFlow(app.applicationContext).collect { value ->
                _enabled.value = value
            }
        }
    }

    fun toggleModo() {
        val newValue = !(_enabled.value ?: false)
        viewModelScope.launch {
            PreferencesManager.saveModo(getApplication(), newValue)
            _enabled.value = newValue
        }
    }
}
