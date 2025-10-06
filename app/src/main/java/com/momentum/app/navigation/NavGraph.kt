package com.momentum.app.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.momentum.app.feature_wellbeing.ui.PuenteEmocionalScreen
import com.momentum.app.feature_wellbeing.ui.DiarioScreen
import com.momentum.app.feature_wellbeing.ui.ChatApoyoScreen
import com.momentum.app.feature_wellbeing.ui.ProgresoScreen
import com.momentum.app.feature_wellbeing.ui.PerfilScreen
import com.momentum.app.feature_wellbeing.viewmodel.BienestarViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

enum class Routes { PuenteEmocional, Diario, Chat, Progreso, Perfil }

@Composable
fun MomentumNavHost() {
    val nav = rememberNavController()
    val sharedViewModel = viewModel<BienestarViewModel>()
    
    NavHost(navController = nav, startDestination = Routes.PuenteEmocional.name) {
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
    }
}