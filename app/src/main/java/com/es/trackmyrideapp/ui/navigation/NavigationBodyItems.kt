package com.es.trackmyrideapp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.data.local.ThemePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun NavigationBodyItems(
    navController: NavController,
    isDarkTheme: Boolean = false,
    onThemeChanged: (Boolean) -> Unit = {},
    scope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState,
    currentDestination: String?
){
    val context = LocalContext.current
    val themePreferences = remember { ThemePreferences(context) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Map -> Home
        NavigationItem(
            icon = Icons.Default.Map,
            title = "Map",
            selected = currentDestination == Home::class.qualifiedName,
            onClick = {
                scope.launch {
                    navigateAndCloseDrawer(
                        destination = Home,
                        navController = navController,
                        scope = scope,
                        drawerState = drawerState
                    )
                }
            }
        )

        // Routes History
        NavigationItem(
            icon = Icons.Default.Navigation,
            title = "Routes History",
            selected = currentDestination == RoutesHistory::class.qualifiedName,
            onClick = {
                scope.launch {
                    navigateAndCloseDrawer(
                        destination = RoutesHistory,
                        navController = navController,
                        scope = scope,
                        drawerState = drawerState
                    )
                }
            }
        )

        // My Vehicles
        NavigationItem(
            icon = Icons.Default.DirectionsCar,
            title = "My Vehicles",
            selected = currentDestination == Vehicles::class.qualifiedName,
            onClick = {
                navigateAndCloseDrawer(
                    destination = Vehicles,
                    navController = navController,
                    scope = scope,
                    drawerState = drawerState
                )
            }
        )

        // My Profile
        NavigationItem(
            icon = Icons.Default.Person,
            title = "My Profile",
            selected = currentDestination == Profile::class.qualifiedName,
            onClick = {
                navigateAndCloseDrawer(
                    destination = Profile,
                    navController = navController,
                    scope = scope,
                    drawerState = drawerState
                )
            }
        )

        // Upgrade to Premium
        NavigationItem(
            customIcon = R.drawable.logo_premium,
            title = "Upgrade to Premium",
            selected = currentDestination == Premium::class.qualifiedName,
            onClick = {
                navigateAndCloseDrawer(
                    destination = Premium,
                    navController = navController,
                    scope = scope,
                    drawerState = drawerState
                )
            }
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )

    // Settings section
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Light Theme toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "Theme",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = if (isDarkTheme) "Dark Theme" else "Light Theme",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Switch(
                checked = !isDarkTheme,
                onCheckedChange = { newValue ->
                    val newTheme = !newValue
                    onThemeChanged(newTheme)
                    themePreferences.setDarkThemeEnabled(newTheme)
                }
            )
        }

        // About us
        NavigationItem(
            icon = Icons.Default.Info,
            title = "About us",
            selected = currentDestination == AboutUs::class.qualifiedName,
            onClick = {
                navigateAndCloseDrawer(
                    destination = AboutUs,
                    navController = navController,
                    scope = scope,
                    drawerState = drawerState
                )
            }

        )
    }
}