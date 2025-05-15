package com.es.trackmyrideapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.es.trackmyrideapp.ui.screens.aboutUsScreen.AboutUsScreen
import com.es.trackmyrideapp.ui.screens.homeScreen.HomeScreen
import com.es.trackmyrideapp.ui.screens.forgotPasswordScreen.ForgotPasswordScreen
import com.es.trackmyrideapp.ui.screens.loginScreen.LoginScreen
import com.es.trackmyrideapp.ui.screens.premiumScreen.PremiumScreen
import com.es.trackmyrideapp.ui.screens.profileScreen.ProfileScreen
import com.es.trackmyrideapp.ui.screens.registerScreen.RegisterScreen
import com.es.trackmyrideapp.ui.screens.routeDetailsScreen.RouteDetailScreen
import com.es.trackmyrideapp.ui.screens.routesHistoryScreen.RoutesHistoryScreen
import com.es.trackmyrideapp.ui.screens.vehiclesScreen.VehiclesScreen

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
                snackbarHostState = snackbarHostState,
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
                snackbarHostState = snackbarHostState
            )
        }

        // Home Screen
        composable<Home> {
            HomeScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }

        // Routes History Screen
        composable<RoutesHistory> {
            RoutesHistoryScreen(
                onViewDetailsClicked = { navController.navigate(RouteDetails) },
                modifier = Modifier.padding(innerPadding)
            )
        }

        // Routes Details Screen
        composable<RouteDetails> {
            RouteDetailScreen(
                modifier = Modifier.padding(innerPadding)
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
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}