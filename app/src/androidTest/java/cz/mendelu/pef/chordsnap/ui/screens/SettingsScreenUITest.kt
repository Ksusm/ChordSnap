package cz.mendelu.pef.chordsnap.ui.screens

import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.mendelu.pef.chordsnap.MainActivity
import cz.mendelu.pef.chordsnap.ui.screens.settings.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SettingsScreenUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: NavHostController

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private fun launchSettingsScreen() {
        composeRule.activity.setContent {
            navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "settings") {
                    composable("settings") {
                        SettingsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    @Test
    fun test_01_settings_displays_all_elements() {
        launchSettingsScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsTitle).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagSettingsBackButton).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagSettingsLanguageItem).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagSettingsThemeItem).assertIsDisplayed()
    }

    @Test
    fun test_02_back_button_navigates() {
        var backCalled = false

        composeRule.activity.setContent {
            navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "settings") {
                    composable("settings") {
                        SettingsScreen(
                            onNavigateBack = { backCalled = true }
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(TestTagSettingsBackButton).performClick()
        composeRule.waitForIdle()

        assert(backCalled)
    }

    @Test
    fun test_03_language_item_opens_dialog() {
        launchSettingsScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsLanguageItem).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsLanguageDialog).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagSettingsLanguageEnglish).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagSettingsLanguageCzech).assertIsDisplayed()
    }

    @Test
    fun test_04_language_dialog_cancel_closes() {
        launchSettingsScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsLanguageItem).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsCancelButton).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsLanguageDialog).assertDoesNotExist()
    }

    @Test
    fun test_05_theme_item_opens_dialog() {
        launchSettingsScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsThemeItem).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsThemeDialog).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagSettingsThemeLight).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagSettingsThemeDark).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagSettingsThemeSystem).assertIsDisplayed()
    }

    @Test
    fun test_06_theme_dialog_cancel_closes() {
        launchSettingsScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsThemeItem).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsCancelButton).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsThemeDialog).assertDoesNotExist()
    }

    @Test
    fun test_07_theme_selection_changes_theme() {
        launchSettingsScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsThemeItem).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsThemeDark).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagSettingsThemeDialog).assertDoesNotExist()
    }
}