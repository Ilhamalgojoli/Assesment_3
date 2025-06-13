package com.ilhamalgojali0081.assesment_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ilhamalgojali0081.assesment_3.ui.theme.Assesment_3Theme
import com.ilhamalgojali0081.assesment_3.ui.theme.screen.MainScreen
import com.ilhamalgojali0081.assesment_3.ui.theme.screen.ResepDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assesment_3Theme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "mainScreen") {
                    composable("mainScreen") {
                        MainScreen(navController = navController)
                    }
                    composable(
                        "recipeDetail/{recipeId}",
                        arguments = listOf(navArgument("recipeId")
                        { type = NavType.StringType })
                    ) { backStackEntry ->
                        val recipeId = backStackEntry.arguments?.getString("recipeId")
                        if (recipeId != null) {
                            ResepDetailScreen(navController = navController, recipeId = recipeId)
                        } else {
                            Text("Error: Recipe ID tidak ditemukan untuk detail.")
                        }
                    }
                }
            }
        }
    }
}