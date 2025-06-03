package com.es.trackmyrideapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.es.trackmyrideapp.ui.screens.aboutUsScreen.AboutUsScreen
import com.es.trackmyrideapp.ui.screens.adminScreen.AdminScreen
import com.es.trackmyrideapp.ui.screens.forgotPasswordScreen.ForgotPasswordScreen
import com.es.trackmyrideapp.ui.screens.homeScreen.HomeScreen
import com.es.trackmyrideapp.ui.screens.loginScreen.LoginScreen
import com.es.trackmyrideapp.ui.screens.premiumScreen.PremiumScreen
import com.es.trackmyrideapp.ui.screens.profileScreen.ProfileScreen
import com.es.trackmyrideapp.ui.screens.registerScreen.RegisterScreen
import com.es.trackmyrideapp.ui.screens.routeDetailsScreen.RouteDetailScreen
import com.es.trackmyrideapp.ui.screens.routesHistoryScreen.RoutesHistoryScreen
import com.es.trackmyrideapp.ui.screens.vehiclesScreen.VehiclesScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    startDestination: String,
    snackbarHostState: SnackbarHostState,
){
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // Login Screen
        composable<Login> {
            LoginScreen(
                navigateToRegister = {
                    navController.navigate(Register){
                        popUpTo(Login) { inclusive = true }
                        launchSingleTop = true
                    } },
                navigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                navigateToForgotPassword = {
                    navController.navigate(ForgotPassword){
                        launchSingleTop = true
                    }
                },
                navigateToAdminScreen ={
                    navController.navigate(AdminScreen) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            )
        }

        // Forgot Password Screen
        composable<ForgotPassword> {
            ForgotPasswordScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }

        // Register Screen
        composable<Register> {
            RegisterScreen(
                navigateToLogin = {
                    navController.navigate(Login){
                        popUpTo(Register) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                snackbarHostState = snackbarHostState,
                navigateToAdminScreen = {
                    navController.navigate(AdminScreen) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            )
        }

        // Home Screen
        composable<Home> {
            HomeScreen(
                modifier = Modifier.padding(innerPadding),
            )
        }

        // Routes History Screen
        composable<RoutesHistory> {
            RoutesHistoryScreen(
                onViewDetailsClicked = { routeId -> navController.navigate(RouteDetails(routeId = routeId)) },
                modifier = Modifier.padding(innerPadding),
                onGetPremiumClicked = {
                    navController.navigate(Premium) {
                        popUpTo<RoutesHistory> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Routes Details Screen
        composable<RouteDetails> { backStackEntry ->
            val routeDetail = backStackEntry.toRoute<RouteDetails>()
            RouteDetailScreen(
                modifier = Modifier.padding(innerPadding),
                idRoute = routeDetail.routeId,
                onGoPremiumClicked = {
                    navController.navigate(Premium) {
                        popUpTo<RouteDetails> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Profile Screen
        composable<Profile> {
            ProfileScreen(
                modifier = Modifier.padding(innerPadding),
                onPremiumScreenClicked = {
                    navController.navigate(Premium) {
                        popUpTo<Profile> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Premium Screen
        composable<Premium> {
            PremiumScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }

        // About us Screen
        composable<AboutUs> {
            AboutUsScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }

        // MyVehicles Screen
        composable<Vehicles> {
            VehiclesScreen(
                modifier = Modifier.padding(innerPadding),
            )
        }

        // Admin Screen
        composable<AdminScreen> {
            AdminScreen(
                modifier = Modifier.padding(innerPadding),
                snackbarHostState = snackbarHostState
            )
        }
    }
}