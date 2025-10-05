package com.momentum.app.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.momentum.app.feature_registration.ui.RegistroScreen
import com.momentum.app.feature_registration.ui.ResumenScreen
import com.momentum.app.feature_registration.ui.ContratoScreen
import com.momentum.app.feature_registration.viewmodel.UsuarioViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

enum class Routes { Registro, Resumen, Contrato }

@Composable
fun MomentumNavHost() {
    val nav = rememberNavController()
    val sharedViewModel = viewModel<UsuarioViewModel>()
    
    NavHost(navController = nav, startDestination = Routes.Registro.name) {
        composable(Routes.Registro.name) { 
            RegistroScreen(navController = nav, viewModel = sharedViewModel) 
        }
        composable(Routes.Resumen.name) { 
            ResumenScreen(
                viewModel = sharedViewModel,
                onBackClick = { nav.popBackStack() }
            ) 
        }
        composable(Routes.Contrato.name) {
            ContratoScreen(
                onBackClick = { nav.popBackStack() }
            )
        }
    }
}