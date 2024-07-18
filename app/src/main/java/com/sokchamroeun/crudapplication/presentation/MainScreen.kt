package com.sokchamroeun.crudapplication.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sokchamroeun.crudapplication.viewmodel.UserViewModel


sealed class Destination(val route: String) {
    object UserScreen : Destination("user_screen/{userId}") {
        fun createRoute(userId: Int) = "user_screen/$userId"
    }

    object ListUser : Destination("list_users_screen")
}

@Composable
fun AppNavHost(
    navController: NavHostController, userViewModel: UserViewModel, paddingValues: PaddingValues
) {
    NavHost(
        navController, startDestination = Destination.ListUser.route
    ) {
        composable(
            route = Destination.UserScreen.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            AddUserScreen(userViewModel, userId, navController)
        }
        composable(Destination.ListUser.route) {
            ListUsersScreen(userViewModel, navController)
        }
    }
}

@Composable
fun MainScreen(userViewModel: UserViewModel) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    Scaffold(floatingActionButton = {
        if (currentRoute == Destination.ListUser.route) {
            FloatingActionButton(onClick = {
                navController.navigate(Destination.UserScreen.createRoute(-1))
            }) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add new user")
            }
        }
    }) { paddingValues ->
        AppNavHost(navController, userViewModel, paddingValues)
    }
}



