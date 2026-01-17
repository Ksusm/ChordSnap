package cz.mendelu.pef.chordsnap.ui.screens

import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cz.mendelu.pef.chordsnap.fake.FakePracticeDao
import cz.mendelu.pef.chordsnap.MainActivity
import cz.mendelu.pef.chordsnap.database.PracticeDao
import cz.mendelu.pef.chordsnap.database.PracticeEntity
import cz.mendelu.pef.chordsnap.navigation.Destination
import cz.mendelu.pef.chordsnap.navigation.NavGraph
import cz.mendelu.pef.chordsnap.ui.screens.home.TestTagHomeNoPracticesMessage
import cz.mendelu.pef.chordsnap.ui.screens.home.TestTagHomePracticesRow
import cz.mendelu.pef.chordsnap.ui.screens.home.TestTagHomeScanFab
import cz.mendelu.pef.chordsnap.ui.screens.home.TestTagHomeSettingsButton
import cz.mendelu.pef.chordsnap.ui.screens.home.TestTagHomeTitle
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import javax.inject.Inject

@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class HomeScreenUITest {

    private lateinit var navController: NavHostController

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var practiceDao: PracticeDao

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    // TEST 1 - Basic UI elements are displayed
    @Test
    fun test_01_homeScreen_displays_basic_elements() {
        launchHomeScreen()
        with(composeRule) {
            onNodeWithTag(TestTagHomeTitle).assertIsDisplayed()
            onNodeWithTag(TestTagHomeSettingsButton).assertIsDisplayed()
            onNodeWithTag(TestTagHomeScanFab).assertIsDisplayed()
            waitForIdle()
            Thread.sleep(500)
        }
    }

    // TEST 2 - Scan FAB click navigates to ScanScreen
    @Test
    fun test_02_scanFab_click_navigates_to_scanScreen() {
        launchHomeScreen()
        with(composeRule) {
            onNodeWithTag(TestTagHomeScanFab).assertIsDisplayed()
            onNodeWithTag(TestTagHomeScanFab).performClick()
            waitForIdle()

            val route = navController.currentBackStackEntry?.destination?.route
            Thread.sleep(500)
            assertTrue(route == Destination.ScanScreen.route)
        }
    }

    // TEST 3 - Settings button click navigates to SettingsScreen
    @Test
    fun test_03_settingsButton_click_navigates_to_settingsScreen() {
        launchHomeScreen()
        with(composeRule) {
            onNodeWithTag(TestTagHomeSettingsButton).assertIsDisplayed()
            onNodeWithTag(TestTagHomeSettingsButton).performClick()
            waitForIdle()

            val route = navController.currentBackStackEntry?.destination?.route
            Thread.sleep(500)
            assertTrue(route == Destination.SettingsScreen.route)
        }
    }

    // TEST 4 - Empty state shows "No practices" message
    @Test
    fun test_04_emptyState_displays_noPractices_message() {
        (practiceDao as FakePracticeDao).setPractices(emptyList())

        launchHomeScreen()
        with(composeRule) {
            waitForIdle()
            Thread.sleep(500)

            onNodeWithTag(TestTagHomeNoPracticesMessage).assertIsDisplayed()
            onNodeWithText("No practices yet").assertIsDisplayed()
        }
    }

    // TEST 5 - Practice cards are displayed when practices exist
    @Test
    fun test_05_practiceCards_displayed_when_practices_exist() {
        val testPractices = listOf(
            PracticeEntity(1, "Strumming basics", "1,2,3"),
            PracticeEntity(2, "Moon river", "4,5,6,7,8")
        )
        (practiceDao as FakePracticeDao).setPractices(testPractices)

        launchHomeScreen()
        with(composeRule) {
            waitForIdle()
            Thread.sleep(500)

            onNodeWithTag(TestTagHomePracticesRow).assertIsDisplayed()
            onNodeWithTag("practice_card_1").assertIsDisplayed()
            onNodeWithTag("practice_card_2").assertIsDisplayed()
        }
    }

    // TEST 6 - Practice card click navigates to PracticeViewScreen
    @Test
    fun test_06_practiceCard_click_navigates_to_practiceView() {
        val testPractices = listOf(
            PracticeEntity(1, "Strumming basics", "1,2,3")
        )
        (practiceDao as FakePracticeDao).setPractices(testPractices)

        launchHomeScreen()
        with(composeRule) {
            waitForIdle()
            Thread.sleep(500)

            onNodeWithTag("practice_card_1").assertIsDisplayed()
            onNodeWithTag("practice_card_1").performClick()
            waitForIdle()

            val route = navController.currentBackStackEntry?.destination?.route
            Thread.sleep(500)
            assertTrue(route == "practice_view/{practiceId}")
        }
    }

    // TEST 7 - Practice card displays correct chord count
    @Test
    fun test_07_practiceCard_displays_correct_chord_count() {
        val testPractices = listOf(
            PracticeEntity(1, "Strumming basics", "1,2,3"),
            PracticeEntity(2, "Single chord", "1")
        )
        (practiceDao as FakePracticeDao).setPractices(testPractices)

        launchHomeScreen()
        with(composeRule) {
            waitForIdle()
            Thread.sleep(500)

            onNodeWithText("3 chords").assertIsDisplayed()
            onNodeWithText("1 chord").assertIsDisplayed()
        }
    }

    // TEST 8 - Multiple practice cards are scrollable
    @Test
    fun test_08_multiplePractices_are_scrollable() {
        val testPractices = listOf(
            PracticeEntity(1, "Practice 1", "1,2"),
            PracticeEntity(2, "Practice 2", "3,4"),
            PracticeEntity(3, "Practice 3", "5,6"),
            PracticeEntity(4, "Practice 4", "7,8")
        )
        (practiceDao as FakePracticeDao).setPractices(testPractices)

        launchHomeScreen()
        with(composeRule) {
            waitForIdle()
            Thread.sleep(500)

            // Verify LazyRow exists and contains cards
            onNodeWithTag(TestTagHomePracticesRow).assertIsDisplayed()
            onNodeWithTag("practice_card_1").assertIsDisplayed()

            // Scroll to see the last card
            onNodeWithTag(TestTagHomePracticesRow).performScrollToNode(
                hasTestTag("practice_card_4")
            )
            waitForIdle()
            onNodeWithTag("practice_card_4").assertIsDisplayed()
        }
    }

    // TEST 9 - Bottom navigation is displayed
    @Test
    fun test_10_bottomNavigation_is_displayed() {
        launchHomeScreen()
        with(composeRule) {
            waitForIdle()
            Thread.sleep(500)

            // Check bottom navigation items
            onNodeWithText("Home").assertIsDisplayed()
            onNodeWithText("Chords").assertIsDisplayed()
            onNodeWithText("Map").assertIsDisplayed()
        }
    }

    // Helper: Launch HomeScreen with navigation
    private fun launchHomeScreen() {
        composeRule.activity.setContent {
            MaterialTheme {
                navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}