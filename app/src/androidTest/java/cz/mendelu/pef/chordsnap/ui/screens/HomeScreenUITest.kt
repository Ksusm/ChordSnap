package cz.mendelu.pef.chordsnap.ui.screens

import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cz.mendelu.pef.chordsnap.MainActivity
import cz.mendelu.pef.chordsnap.database.PracticeDao
import cz.mendelu.pef.chordsnap.database.PracticeEntity
import cz.mendelu.pef.chordsnap.fake.FakePracticeDao
import cz.mendelu.pef.chordsnap.navigation.Destination
import cz.mendelu.pef.chordsnap.navigation.NavGraph
import cz.mendelu.pef.chordsnap.ui.screens.home.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import javax.inject.Inject

@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class HomeScreenUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var practiceDao: PracticeDao

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    private fun launchHomeScreen() {
        composeRule.activity.setContent {
            MaterialTheme {
                navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }

    @Test
    fun test_01_homeScreen_displays_title_and_bottomBar() {
        launchHomeScreen()
        composeRule.onNodeWithTag(TestTagHomeTitle).assertIsDisplayed()
        composeRule.onNodeWithTag("bottom_nav_home").assertIsDisplayed()
        composeRule.onNodeWithTag("bottom_nav_chords").assertIsDisplayed()
        composeRule.onNodeWithTag("bottom_nav_map").assertIsDisplayed()
    }

    @Test
    fun test_02_scanFab_navigates_to_scanScreen() {
        launchHomeScreen()
        composeRule.onNodeWithTag(TestTagHomeScanFab).performClick()
        composeRule.waitForIdle()
        assertTrue(navController.currentBackStackEntry?.destination?.route == Destination.ScanScreen.route)
    }

    @Test
    fun test_03_settingsButton_navigates_to_settingsScreen() {
        launchHomeScreen()
        composeRule.onNodeWithTag(TestTagHomeSettingsButton).performClick()
        composeRule.waitForIdle()
        assertTrue(navController.currentBackStackEntry?.destination?.route == Destination.SettingsScreen.route)
    }

    @Test
    fun test_04_emptyState_displays_noPractices_message() {
        (practiceDao as FakePracticeDao).setPractices(emptyList())
        launchHomeScreen()
        composeRule.onNodeWithTag(TestTagHomeNoPracticesMessage).assertIsDisplayed()
    }

    @Test
    fun test_05_practiceCards_display_correct_info() {
        val testPractices = listOf(
            PracticeEntity(1, "Strumming basics", "1,2,3")
        )
        (practiceDao as FakePracticeDao).setPractices(testPractices)
        launchHomeScreen()
        composeRule.onNodeWithTag("practice_card_1").assertIsDisplayed()
    }

    @Test
    fun test_06_practiceCard_click_navigates_to_practiceView() {
        val testPractices = listOf(PracticeEntity(1, "Moon River", "1,2"))
        (practiceDao as FakePracticeDao).setPractices(testPractices)
        launchHomeScreen()
        composeRule.onNodeWithTag("practice_card_1").performClick()
        composeRule.waitForIdle()
        assertTrue(navController.currentBackStackEntry?.destination?.route == "practice_view/{practiceId}")
    }

    @Test
    fun test_07_multiplePractices_are_scrollable() {
        val testPractices = (1..6).map { PracticeEntity(it.toLong(), "Practice $it", "1") }
        (practiceDao as FakePracticeDao).setPractices(testPractices)
        launchHomeScreen()
        composeRule.onNodeWithTag(TestTagHomePracticesRow).performScrollToNode(hasTestTag("practice_card_6"))
        composeRule.onNodeWithTag("practice_card_6").assertIsDisplayed()
    }
}