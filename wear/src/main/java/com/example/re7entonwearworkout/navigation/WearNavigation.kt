package com.example.re7entonwearworkout.navigation

import androidx.compose.runtime.Composable
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.re7entonwearworkout.hydration.HydrationScreen
import com.example.re7entonwearworkout.timer.TimerScreen

@Composable
fun WearNavigation() {
    val navController = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController    = navController,
        startDestination = Destination.Timer.route
    ) {
        // bind the Timer screen
        composable(Destination.Timer.route) {
            TimerScreen(
                onHydrationClick = {
                    navController.navigate(Destination.Hydration.route)
                }
            )
        }
        // bind the Hydration screen
        composable(Destination.Hydration.route) {
            HydrationScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}