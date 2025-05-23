package com.es.trackmyrideapp.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.es.trackmyrideapp.LocalSessionViewModel


@Composable
fun NavigationWrapper(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    startDestination: String
){
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val sessionViewModel = LocalSessionViewModel.current


    // Obtener la ruta actual
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    // Controlar Screens
    val (isAuthScreen, isSubScreen, isHomeScreen) = remember(currentDestination) {
        Triple(
            currentDestination in listOf(Login::class.qualifiedName, Register::class.qualifiedName),
            currentDestination?.startsWith(RouteDetails::class.qualifiedName ?: "") == true || currentDestination == ForgotPassword::class.qualifiedName,
            currentDestination == Home::class.qualifiedName
        )
    }

    // Controlar visibilidad UI
    val showAppBars = !isAuthScreen
    val showDrawer = showAppBars && !isSubScreen
    val showBackButton = isSubScreen

    // Paddings
    val windowInsets = WindowInsets.systemBars
    val statusBarPadding = windowInsets.asPaddingValues().calculateTopPadding()

    val nameState by remember { mutableStateOf("Adrian Arroyo") }
    val userPlanState by remember { mutableStateOf("Free Account") }

    /**
     * Log Pila navegacion a modo DEV
     */
    val navStack = remember { mutableStateListOf<String>() }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val currentRoute = backStackEntry.destination.route ?: return@collect

            // Si la ruta ya estaba antes (por popUpTo o back), eliminamos desde ahí
            val index = navStack.indexOf(currentRoute)
            if (index != -1 && index != navStack.lastIndex) {
                navStack.removeRange(index + 1, navStack.size)
            } else if (navStack.lastOrNull() != currentRoute) {
                navStack.add(currentRoute)
            }

            Log.d("NavStack", "Pila actual: $navStack")
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showDrawer && !isHomeScreen, // Evitar abrir el drawer donde no toca
        drawerContent = {
            if (showDrawer) {
                DrawerContent(
                    modifier = Modifier.padding(top = statusBarPadding),
                    navController = navController,
                    drawerState = drawerState,
                    userName = nameState ,
                    userPlan = userPlanState,
                    currentDestination = currentDestination,
                    isDarkTheme = isDarkTheme,
                    onThemeChanged = onThemeChanged
                )
            }
        }
    ) {
        if (showAppBars){
            Scaffold(
                // Quitar padding por defecto superior
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                },
                topBar = {
                    AppTopBar(
                        currentDestination = currentDestination,
                        scope = scope,
                        drawerState = drawerState,
                        navigateToHomeClicked = {
                            navController.navigate(Home) {
                                popUpTo<Home> { inclusive = true }
                                launchSingleTop = true
                            } },
                        navigateToHistoryClicked = {
                            navController.navigate(RoutesHistory) {
                                popUpTo<RoutesHistory> { inclusive = true }
                                launchSingleTop = true
                            } },
                        showBackButton = showBackButton,
                        onBackClicked = { navController.popBackStack() },
                        onMapTypeChanged = { mapType ->
                            sessionViewModel.setMapType(mapType)
                        },
                        currentMapType = sessionViewModel.mapType.collectAsState().value,
                        onVehicleTypeChanged = { vehicle ->
                            sessionViewModel.selectVehicle(vehicle)
                        },
                        currentVehicleType = sessionViewModel.selectedVehicle.collectAsState().value
                    )
                }
            ) { innerPadding ->

                MainNavHost(
                    navController = navController,
                    innerPadding = innerPadding,
                    startDestination = startDestination,
                    snackbarHostState = snackbarHostState
                )
            }
        }else{
            MainNavHost(
                navController = navController,
                innerPadding = PaddingValues(0.dp),
                startDestination = startDestination,
                snackbarHostState = snackbarHostState
            )
        }

    }
}


