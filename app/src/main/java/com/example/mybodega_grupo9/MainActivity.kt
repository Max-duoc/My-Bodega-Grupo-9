package com.example.mybodega_grupo9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mybodega_grupo9.navigation.AppNavigation // Asegúrate de que este import sea correcto

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Envuelve tu navegación con el tema de la app
                AppNavigation()  // 👈 Aquí llamas a la navegación que creamos

        }
    }
}
