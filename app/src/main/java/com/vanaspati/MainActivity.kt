package com.vanaspati

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vanaspati.ui.screens.HomeScreen
import com.vanaspati.ui.screens.ResultsScreen
import com.vanaspati.ui.screens.SettingsScreen
import com.vanaspati.ui.theme.VanaspatiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VanaspatiTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    VanaspatiNavHost(navController)
                }
            }
        }
    }
}

@Composable
private fun VanaspatiNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onOpenSettings = { navController.navigate("settings") },
                onOpenResults = { query ->
                    val encoded = java.net.URLEncoder.encode(query ?: "", Charsets.UTF_8.name()).replace("+", "%20")
                    navController.navigate("results?query=$encoded")
                }
            )
        }
        composable("results?query={query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query")
            ResultsScreen(onBack = { navController.popBackStack() }, initialQuery = query)
        }
        composable("settings") { SettingsScreen(onBack = { navController.popBackStack() }) }
    }
}
