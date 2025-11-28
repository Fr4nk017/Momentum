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
import com.momentum.app.ui.screens.profile.UserProfileScreen
import com.momentum.app.ui.screens.friends.FriendsScreen
import com.momentum.app.ui.screens.friends.SearchFriendsScreen
import com.momentum.app.ui.screens.community.CommunityScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.momentum.app.ui.animations.*
import com.momentum.app.viewmodel.DiaryViewModel
import com.momentum.app.viewmodel.ProgressViewModel
import com.momentum.app.viewmodel.ChatViewModel
import com.momentum.app.viewmodel.HomeViewModel
import androidx.compose.runtime.remember
import com.momentum.app.data.remote.moods.BackendRetrofitInstance
import com.momentum.app.data.repository.MoodRepository
import com.momentum.app.ui.screens.moods.RemoteMoodsScreen
import com.momentum.app.ui.viewmodel.MoodViewModel
import com.momentum.app.ui.viewmodel.MoodViewModelFactory


enum class Routes { PuenteEmocional, Diario, Chat, Progreso, Perfil, UserProfile, Community, Friends, Places, Hiking, HikingHistory, OutdoorActivities,RemoteMoods }

object AuthRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PASSWORD_RECOVERY = "password_recovery"
    const val HOME = "home"
}

@Composable
fun MomentumNavHost() {
    val nav = rememberNavController()
    val sharedViewModel = viewModel<BienestarViewModel>()
    val diaryViewModel = viewModel<DiaryViewModel>()
    val progressViewModel = viewModel<ProgressViewModel>()
     val chatViewModel = viewModel<ChatViewModel>()
    val homeViewModel = viewModel<HomeViewModel>()
    // --- Backend Spring Boot + MongoDB (Moods) ---
    val moodRepository = remember { MoodRepository(BackendRetrofitInstance.api) }

    // por ahora un userId fijo, luego lo sacamos del login o DataStore
    val userId = "gustavo@example.com"

    val moodViewModel: MoodViewModel = viewModel(
        factory = MoodViewModelFactory(moodRepository, userId)
    )

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
                    nav.navigate(Routes.PuenteEmocional.name) {
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
        composable(
            Routes.PuenteEmocional.name,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) { 
            PuenteEmocionalScreen(navController = nav, viewModel = sharedViewModel, homeViewModel = homeViewModel) 
        }
        composable(
            Routes.Diario.name,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) { 
            DiarioScreen(
                navController = nav,
                viewModel = sharedViewModel,
                diaryViewModel = diaryViewModel
            )
        }
        composable(
            Routes.Chat.name,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
                ChatApoyoScreen(
                    navController = nav,
                    viewModel = sharedViewModel,
                    chatViewModel = chatViewModel
                )
        }
        composable(
            Routes.Progreso.name,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            ProgresoScreen(
                navController = nav,
                viewModel = sharedViewModel,
                progressViewModel = progressViewModel
            )
        }
        composable(
            Routes.Perfil.name,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            PerfilScreen(navController = nav, viewModel = sharedViewModel)
        }

        composable(
            Routes.Places.name,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            com.momentum.app.ui.screens.places.PlacesScreen(navController = nav)
        }
        
        // New social features routes
        composable(
            Routes.UserProfile.name,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            UserProfileScreen(navController = nav)
        }
        composable(
            Routes.Community.name,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            CommunityScreen(navController = nav)
        }
        composable(
            Routes.Friends.name,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            FriendsScreen(navController = nav)
        }
        composable(
            "friends",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            FriendsScreen(navController = nav)
        }
        composable(
            "search_friends",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            SearchFriendsScreen(navController = nav)
        }
        composable(
            Routes.Hiking.name,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            com.momentum.app.ui.screens.HikingScreen(
                onNavigateBack = { nav.popBackStack() }
            )
        }
        composable(
            Routes.HikingHistory.name,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            com.momentum.app.ui.screens.HikingHistoryScreen(
                onNavigateBack = { nav.popBackStack() }
            )
        }
        composable(
            Routes.OutdoorActivities.name,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {

            com.momentum.app.ui.screens.OutdoorActivitiesScreen(navController = nav)
        }
        composable(Routes.RemoteMoods.name) {
            RemoteMoodsScreen(
                viewModel = moodViewModel   // 👈 usas el VM que creaste arriba
            )
        }
    }


}
