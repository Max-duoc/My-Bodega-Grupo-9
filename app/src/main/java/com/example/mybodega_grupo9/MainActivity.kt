package com.example.mybodega_grupo9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mybodega_grupo9.ui.theme.HomeScreen
import com.example.mybodega_grupo9.ui.theme.Screen.AdaptiveHome


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdaptiveHome( {})  // 👈 Aquí llamas a tu pantalla principal
        }
    }
}
