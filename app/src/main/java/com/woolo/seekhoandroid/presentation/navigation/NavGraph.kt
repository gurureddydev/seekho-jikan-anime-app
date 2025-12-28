package com.woolo.seekhoandroid.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.woolo.seekhoandroid.presentation.screen.anime_detail.AnimeDetailScreen
import com.woolo.seekhoandroid.presentation.screen.anime_list.AnimeListScreen
import com.woolo.seekhoandroid.presentation.screen.splash.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object AnimeList : Screen("anime_list")
    object AnimeDetail : Screen("anime_detail/{malId}") {
        fun createRoute(malId: Int) = "anime_detail/$malId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToAnimeList = {
                    navController.navigate(Screen.AnimeList.route) {
                        // Clear splash from back stack
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AnimeList.route) {
            AnimeListScreen(
                onAnimeClick = { malId ->
                    navController.navigate(Screen.AnimeDetail.createRoute(malId))
                }
            )
        }
        composable(
            route = Screen.AnimeDetail.route,
            arguments = listOf(navArgument("malId") { type = NavType.IntType })
        ) { backStackEntry ->
            val malId = backStackEntry.arguments?.getInt("malId") ?: -1
            AnimeDetailScreen(
                malId = malId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

