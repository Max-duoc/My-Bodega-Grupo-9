package com.example.mybodega_grupo9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.mybodega_grupo9.navigation.AppNavGraph
import com.example.mybodega_grupo9.ui.theme.MyBodegaTheme
import com.example.mybodega_grupo9.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Observa el estado del modo oscuro
            val isDarkMode = themeViewModel.isDarkMode.collectAsState()
            val navController = rememberNavController()

            // Envuelve toda la app con el tema dinámico
            MyBodegaTheme(darkTheme = isDarkMode.value) {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { themeViewModel.toggleTheme() },
                            containerColor = if (isDarkMode.value)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.secondary
                        ) {
                            Icon(
                                imageVector = if (isDarkMode.value)
                                    Icons.Default.LightMode
                                else
                                    Icons.Default.DarkMode,
                                contentDescription = "Cambiar tema",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                ) { padding ->
                    AppNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}


