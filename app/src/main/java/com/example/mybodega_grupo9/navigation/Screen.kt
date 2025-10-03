package com.example.mybodega_grupo9.navigation

sealed class Screen(val route: String) {
    object Home: Screen("home")
    object AddItem: Screen("add_item")
    object Details: Screen("details/{itemId}") {
        fun createRoute(id: Int) = "details/$id"
    }
}
