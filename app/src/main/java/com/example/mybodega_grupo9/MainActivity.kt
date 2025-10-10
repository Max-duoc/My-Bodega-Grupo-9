package com.example.mybodega_grupo9


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.mybodega_grupo9.navigation.MyBodegaNavHost
import com.example.mybodega_grupo9.ui.anim.ModeScreen
import com.example.mybodega_grupo9.ui.screen.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MyBodegaNavHost(navController = navController)
        }
    }
}

