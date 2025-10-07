package com.example.mybodega_grupo9.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

// DataStore configurado como extensión del Context
private val Context.dataStore by preferencesDataStore("settings")

object PreferencesManager {
    private val MODE_KEY = booleanPreferencesKey("modo_especial")

    suspend fun saveModo(context: Context, enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[MODE_KEY] = enabled }
    }

    fun getModoFlow(context: Context) = context.dataStore.data.map { prefs ->
        prefs[MODE_KEY] ?: false
    }
}