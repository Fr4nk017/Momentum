package com.momentum.app.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.momentum.app.ui.screens.PuenteEmocionalScreen
import com.momentum.app.ui.screens.DiarioScreen
import com.momentum.app.ui.screens.ChatApoyoScreen
import com.momentum.app.ui.screens.ProgresoScreen
import com.momentum.app.ui.screens.PerfilScreen
import com.momentum.app.ui.screens.BienestarViewModel
import com.momentum.app.ui.screens.ClientProfileScreen
import androidx.lifecycle.viewmodel.compose.viewModel

enum class Routes { PuenteEmocional, Diario, Chat, Progreso, Perfil }

object AuthRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PASSWORD_RECOVERY = "password_recovery"
    const val HOME = "home"
    const val CLIENT_PROFILE = "client_profile"
}

@Composable
fun MomentumNavHost() {
    val nav = rememberNavController()
    val sharedViewModel = viewModel<BienestarViewModel>()
    
    NavHost(navController = nav, startDestination = AuthRoutes.LOGIN) {
        // Authentication routes
        composable(AuthRoutes.LOGIN) {
            com.momentum.app.ui.screens.login.LoginScreen(
                onSuccess = { email -> 
                    sharedViewModel.inicializarConUsuario(email)
                    nav.navigate(Routes.PuenteEmocional.name) {
                        popUpTo(AuthRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateRegister = { nav.navigate(AuthRoutes.REGISTER) },
                onNavigatePasswordRecovery = { nav.navigate(AuthRoutes.PASSWORD_RECOVERY) }
            )
        }
        composable(AuthRoutes.REGISTER) {
            com.momentum.app.ui.screens.register.RegisterScreen(
                onSuccess = { email, nombre, _ -> 
                    sharedViewModel.inicializarConRegistro(email, nombre)
                    nav.navigate(AuthRoutes.CLIENT_PROFILE) {
                        popUpTo(AuthRoutes.REGISTER) { inclusive = true }
                    }
                }
            )
        }
        composable(AuthRoutes.PASSWORD_RECOVERY) {
            com.momentum.app.ui.screens.password.PasswordRecoveryScreen(
                onBackToLogin = { 
                    nav.popBackStack()
                }
            )
        }
        
        // Main app routes
        composable(Routes.PuenteEmocional.name) { 
            PuenteEmocionalScreen(navController = nav, viewModel = sharedViewModel) 
        }
        composable(Routes.Diario.name) { 
            DiarioScreen(navController = nav, viewModel = sharedViewModel) 
        }
        composable(Routes.Chat.name) {
            ChatApoyoScreen(navController = nav, viewModel = sharedViewModel)
        }
        composable(Routes.Progreso.name) {
            ProgresoScreen(navController = nav, viewModel = sharedViewModel)
        }
        composable(Routes.Perfil.name) {
            PerfilScreen(navController = nav, viewModel = sharedViewModel)
        }
        composable(AuthRoutes.CLIENT_PROFILE) {
            ClientProfileScreen(navController = nav)
        }
    }
}