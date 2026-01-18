package cz.mendelu.pef.chordsnap.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import cz.mendelu.pef.chordsnap.R
import cz.mendelu.pef.chordsnap.navigation.Destination
import cz.mendelu.pef.chordsnap.ui.theme.*
import cz.mendelu.pef.chordsnap.ui.theme.GradientUtils.bottomBarGradient

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    @StringRes val labelRes: Int,
    val testTag: String
) {
    object Home : BottomNavItem(
        route = Destination.HomeScreen.route,
        icon = Icons.Default.Home,
        labelRes = R.string.nav_home,
        testTag = "bottom_nav_home"
    )
    object Chords : BottomNavItem(
        route = Destination.ChordsLibraryScreen.route,
        icon = Icons.AutoMirrored.Filled.List,
        labelRes = R.string.nav_chords,
        testTag = "bottom_nav_chords"
    )
    object Map : BottomNavItem(
        route = Destination.MapScreen.route,
        icon = Icons.Default.Place,
        labelRes = R.string.nav_map,
        testTag = "bottom_nav_map"
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

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.bottomBarHeight)
            .background(
                brush = bottomBarGradient(
                    startColor = MaterialTheme.colorScheme.tertiary,
                    endColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ),
        color = androidx.compose.ui.graphics.Color.Transparent,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = bottomBarGradient(
                        startColor = MaterialTheme.colorScheme.tertiary,
                        endColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    modifier = Modifier.testTag(item.testTag),
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.labelRes),
                            modifier = Modifier.size(Dimensions.iconSizeMedium)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(item.labelRes),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selected = isSelected,
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
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryPurple,
                        selectedTextColor = PrimaryPurple,
                        unselectedIconColor = TextUnselected,
                        unselectedTextColor = TextUnselected,
                        indicatorColor = SurfaceWhite
                    )
                )
            }
        }
    }
}