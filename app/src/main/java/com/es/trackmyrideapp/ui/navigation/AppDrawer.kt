package com.es.trackmyrideapp.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.data.local.ThemePreferences
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



@Composable
fun UserProfileSection(
    userName: String,
    userPlan: String,
    userProfileImageUrl: String? = null
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile avatar
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (userProfileImageUrl != null) {
                AsyncImage(
                    model = userProfileImageUrl,
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

//            Icon(
//                imageVector = Icons.Default.Person,
//                contentDescription = "Profile",
//                tint = Color.White,
//                modifier = Modifier.size(32.dp)
//            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // User name
        Text(
            text = userName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // User plan
        Text(
            text = userPlan,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}



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



@Composable
fun NavigationItem(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    customIcon: Int? = null, // Imagen de Drawable
    title: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit,
    selected: Boolean = false,
) {

    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
    val finalIconTint = if (selected) MaterialTheme.colorScheme.primary else iconTint
    val finalTextColor = if (selected) MaterialTheme.colorScheme.primary else textColor


    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            // Mostrar el icono personalizado de la carpeta Drawable
            if (customIcon != null){
                Image(
                    painter = painterResource(id = customIcon),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp)
                )
            }else{
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = finalIconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = finalTextColor
            )
        }
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