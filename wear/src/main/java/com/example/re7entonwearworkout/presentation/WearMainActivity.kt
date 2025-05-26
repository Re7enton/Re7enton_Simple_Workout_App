/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.re7entonwearworkout.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.re7entonwearworkout.hydration.HydrationScreen
import com.example.re7entonwearworkout.presentation.theme.Re7entonWearWorkoutTheme
import com.example.re7entonwearworkout.timer.TimerScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WearMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Re7entonWearWorkoutTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "timer") {
                    composable("timer") {
                        TimerScreen()
                    }
                    composable("hydration") {
                        HydrationScreen()
                    }
                }
            }
        }
    }
}