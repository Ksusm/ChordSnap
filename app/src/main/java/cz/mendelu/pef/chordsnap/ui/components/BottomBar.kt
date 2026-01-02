package cz.mendelu.pef.chordsnap.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import cz.mendelu.pef.chordsnap.navigation.Destination

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(
        route = Destination.HomeScreen.route,
        icon = Icons.Default.Home,
        label = "Home"
    )
    object Chords : BottomNavItem(
        route = Destination.ChordsLibraryScreen.route,
        icon = Icons.Default.List,
        label = "Chords"
    )
    object Map : BottomNavItem(
        route = Destination.MapScreen.route,
        icon = Icons.Default.Place,
        label = "Map"
    )
}

@Composable
fun BottomBar(
    navController: NavController,
    currentRoute: String?
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Chords,
        BottomNavItem.Map
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}