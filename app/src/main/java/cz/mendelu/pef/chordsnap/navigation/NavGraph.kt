package cz.mendelu.pef.chordsnap.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import cz.mendelu.pef.chordsnap.ui.screens.chorddetail.ChordDetailScreen
import cz.mendelu.pef.chordsnap.ui.screens.chordslibrary.ChordsLibraryScreen
import cz.mendelu.pef.chordsnap.ui.screens.home.HomeScreen
import cz.mendelu.pef.chordsnap.ui.screens.map.MapScreen
import cz.mendelu.pef.chordsnap.ui.screens.practice.PracticeCreationScreen
import cz.mendelu.pef.chordsnap.ui.screens.practice.PracticeViewScreen
import cz.mendelu.pef.chordsnap.ui.screens.scan.ScanScreen
import cz.mendelu.pef.chordsnap.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = Destination.HomeScreen.route
    ) {
        // Home Screen
        composable(route = Destination.HomeScreen.route) {
            HomeScreen(
                navController = navController,
                currentRoute = currentRoute,
                onNavigateToScan = {
                    navController.navigate(Destination.ScanScreen.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Destination.SettingsScreen.route)
                }
            )
        }

        // Scan Screen
        composable(route = Destination.ScanScreen.route) {
            ScanScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onChordRecognized = { chordId ->
                    navController.navigate(Destination.ChordDetailScreen.createRoute(chordId)) {
                        popUpTo(Destination.HomeScreen.route)
                    }
                }
            )
        }

        // Chords Library Screen
        composable(route = Destination.ChordsLibraryScreen.route) {
            ChordsLibraryScreen(
                navController = navController,
                currentRoute = currentRoute,
                onNavigateToChordDetail = { chordId ->
                    navController.navigate(Destination.ChordDetailScreen.createRoute(chordId))
                },
                onNavigateToPracticeCreation = { selectedChordIds ->
                    val chordIdsString = selectedChordIds.joinToString(",")
                    navController.navigate(
                        Destination.PracticeCreationScreen.createRoute(
                            practiceId = null,
                            chordIds = chordIdsString
                        )
                    )
                }
            )
        }

        // Chord Detail Screen
        composable(
            route = Destination.ChordDetailScreen.route,
            arguments = listOf(
                navArgument("chordId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val chordId = backStackEntry.arguments?.getString("chordId") ?: ""
            ChordDetailScreen(
                chordId = chordId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Practice Creation Screen
        composable(
            route = Destination.PracticeCreationScreen.route,
            arguments = listOf(
                navArgument("practiceId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("chordIds") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val practiceIdString = backStackEntry.arguments?.getString("practiceId")
            val practiceId = practiceIdString?.toLongOrNull()
            val chordIdsString = backStackEntry.arguments?.getString("chordIds") ?: ""
            val selectedChordIds = if (chordIdsString.isNotBlank()) {
                chordIdsString.split(",")
            } else {
                emptyList()
            }

            PracticeCreationScreen(
                practiceId = practiceId,
                selectedChordIds = selectedChordIds,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaved = {
                    navController.popBackStack()
                }
            )
        }

        // Map Screen
        composable(route = Destination.MapScreen.route) {
            MapScreen(
                navController = navController,
                currentRoute = currentRoute
            )
        }

        // Settings Screen
        composable(route = Destination.SettingsScreen.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}