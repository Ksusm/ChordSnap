package cz.mendelu.pef.chordsnap.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import cz.mendelu.pef.chordsnap.R
import cz.mendelu.pef.chordsnap.navigation.Destination

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    @StringRes val labelRes: Int
) {
    object Home : BottomNavItem(
        route = Destination.HomeScreen.route,
        icon = Icons.Default.Home,
        labelRes = R.string.nav_home
    )
    object Chords : BottomNavItem(
        route = Destination.ChordsLibraryScreen.route,
        icon = Icons.AutoMirrored.Filled.List,
        labelRes = R.string.nav_chords
    )
    object Map : BottomNavItem(
        route = Destination.MapScreen.route,
        icon = Icons.Default.Place,
        labelRes = R.string.nav_map
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
                        contentDescription = stringResource(item.labelRes)
                    )
                },
                label = { Text(stringResource(item.labelRes)) },
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