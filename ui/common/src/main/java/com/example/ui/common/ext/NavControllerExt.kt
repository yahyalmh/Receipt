package com.example.ui.common.ext

import androidx.navigation.NavController
import androidx.navigation.NavOptions

fun NavController.popCurrentTo(route: String) {
    val navOptions = NavOptions
        .Builder()
        .setPopUpTo(currentDestination!!.route, true)
        .build()
    navigate(route, navOptions)
}