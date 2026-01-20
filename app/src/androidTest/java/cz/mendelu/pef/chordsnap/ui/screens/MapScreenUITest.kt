package cz.mendelu.pef.chordsnap.ui.screens

import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import cz.mendelu.pef.chordsnap.MainActivity
import cz.mendelu.pef.chordsnap.navigation.NavGraph
import cz.mendelu.pef.chordsnap.ui.screens.map.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MapScreenUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private fun launchMapScreen() {
        composeRule.activity.setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
                navController.navigate("map")
            }
        }
    }

    @Test
    fun test_01_map_displays_title_and_legend_button() {
        launchMapScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagMapTitle).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagMapLegendButton).assertIsDisplayed()
    }

    @Test
    fun test_02_map_legend_button_shows_legend() {
        launchMapScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagMapLegendButton).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagMapLegendCard).assertIsDisplayed()
    }

    @Test
    fun test_03_map_legend_close_button_hides_legend() {
        launchMapScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagMapLegendButton).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagMapLegendClose).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagMapLegendCard).assertDoesNotExist()
    }

    @Test
    fun test_04_map_content_is_displayed() {
        launchMapScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagMapContent).assertExists()
    }
}