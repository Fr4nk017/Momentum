package com.momentum.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.momentum.app.feature_wellbeing.ui.ChatApoyoScreen
import com.momentum.app.feature_wellbeing.ui.DiarioScreen
import com.momentum.app.feature_wellbeing.ui.PerfilScreen
import com.momentum.app.feature_wellbeing.ui.ProgresoScreen
import com.momentum.app.feature_wellbeing.ui.PuenteEmocionalScreen
import com.momentum.app.feature_wellbeing.viewmodel.BienestarViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.momentum.app.ui.screens.login.LoginScreen
import com.momentum.app.ui.screens.register.RegisterScreen
import com.momentum.app.ui.screens.profile.ProfileScreen

sealed class Route(val route: String) {
    // Auth routes
    data object Login : Route("login")
    data object Register : Route("register")
    
    // Main navigation routes
    data object Home : Route("home") {
        val destination = PuenteEmocional.route
    }
    data object Profile : Route("profile")

    // Feature routes
    data object PuenteEmocional : Route("puente_emocional")
    data object Diario : Route("diario")
    data object Chat : Route("chat")
    data object Progreso : Route("progreso")
}

@Composable
fun MomentumNavHost(
    modifier: Modifier = Modifier,
    startDestination: String,
    onLoggedIn: () -> Unit,
    onLogout: () -> Unit
) {
    val nav = rememberNavController()
    val sharedViewModel = viewModel<BienestarViewModel>()
    
    NavHost(
        modifier = modifier,
        navController = nav,
        startDestination = startDestination
    ) {
        // Auth routes
        composable(Route.Login.route) {
            LoginScreen(
                onSuccess = onLoggedIn,
                onNavigateRegister = { 
                    nav.navigate(Route.Register.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.Register.route) {
            RegisterScreen(
                onSuccess = onLoggedIn
            )
        }

        // Handle Home route redirection
        composable(Route.Home.route) {
            nav.navigate(Route.Home.destination) {
                popUpTo(nav.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }

        // Main feature routes
        composable(Route.PuenteEmocional.route) { 
            PuenteEmocionalScreen(
                navController = nav, 
                viewModel = sharedViewModel
            )
        }
        composable(Route.Diario.route) { 
            DiarioScreen(
                navController = nav, 
                viewModel = sharedViewModel
            )
        }
        composable(Route.Chat.route) {
            ChatApoyoScreen(
                navController = nav, 
                viewModel = sharedViewModel
            )
        }
        composable(Route.Progreso.route) {
            ProgresoScreen(
                navController = nav, 
                viewModel = sharedViewModel
            )
        }

        // Profile route
        composable(Route.Profile.route) {
            ProfileScreen(
                onNavigateBack = { nav.popBackStack() },
                onLogout = onLogout
            )
        }
    }
}