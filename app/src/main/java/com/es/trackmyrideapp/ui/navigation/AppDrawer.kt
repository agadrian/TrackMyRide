package com.es.trackmyrideapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.es.trackmyrideapp.ui.viewmodels.SessionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope = rememberCoroutineScope(),
    isDarkTheme: Boolean = false,
    onThemeChanged: (Boolean) -> Unit = {},
    userName: String,
    userPlan: String,
    currentDestination: String?,
    userProfileImageUrl: String?
) {
    val sessionViewModel: SessionViewModel = hiltViewModel()

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Close button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        drawerState.close()
                    }
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Drawer",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // User profile section
        UserProfileSection(
            userName = userName,
            userPlan = userPlan,
            userProfileImageUrl = userProfileImageUrl
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Navigation items
        NavigationBodyItems(
            navController = navController,
            isDarkTheme = isDarkTheme,
            onThemeChanged = onThemeChanged,
            scope = rememberCoroutineScope(),
            drawerState = drawerState,
            currentDestination = currentDestination
        )


        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        //Spacer(Modifier.weight(1f))

        // Log out
        NavigationItem(
            icon = Icons.AutoMirrored.Filled.Logout,
            title = "Log Out",
            iconTint = Color.Red,
            textColor = Color.Red,
            selected = false,
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = {
                scope.launch {
                    drawerState.close()
                    sessionViewModel.logout()
                    navController.navigate(Login) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}





/**
 * Navega y cierra el drawer evitando hacerlo si es a la pantalla actual
 */
fun navigateAndCloseDrawer(
    destination: Any,
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val targetRoute = destination::class.qualifiedName

    if (currentRoute != targetRoute && targetRoute != null) {
        navController.navigate(targetRoute) {
            // Elimina todas las pantallas anteriores excepto Home
            popUpTo(Home::class.qualifiedName ?: "") {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    scope.launch {
        drawerState.close()
    }
}