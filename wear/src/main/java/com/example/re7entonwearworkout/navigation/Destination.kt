package com.example.re7entonwearworkout.navigation

sealed class Destination(val route: String) {
    object Timer       : Destination("timer")
    object Hydration   : Destination("hydration")
}