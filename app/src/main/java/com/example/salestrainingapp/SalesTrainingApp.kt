package com.example.salestrainingapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SalesTrainingApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController) }
        composable("dashboard") { DashboardScreen(navController) }
        composable("product_selection") { ProductSelectionScreen(navController) }
        composable("chat/{productName}/{personName}") { backStackEntry ->
            val productName = backStackEntry.arguments?.getString("productName") ?: ""
            val personName = backStackEntry.arguments?.getString("personName") ?: ""
            ChatScreen(navController, productName, personName)
            }
    }
}