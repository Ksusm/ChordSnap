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
import cz.mendelu.pef.chordsnap.database.ChordDao
import cz.mendelu.pef.chordsnap.database.ChordEntity
import cz.mendelu.pef.chordsnap.fake.FakeChordDao
import cz.mendelu.pef.chordsnap.ui.screens.chordslibrary.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import javax.inject.Inject

@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ChordsLibraryScreenUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var chordDao: ChordDao

    private lateinit var navController: NavHostController

    private val testChords = listOf(
        ChordEntity("1", "C major", "Do mayor", "C", "major", "url1"),
        ChordEntity("2", "G minor", "Sol menor", "G", "minor", "url2"),
        ChordEntity("3", "D major", "Re mayor", "D", "major", "url3"),
        ChordEntity("4", "A minor", "La menor", "A", "minor", "url4"),
        ChordEntity("5", "E major", "Mi mayor", "E", "major", "url5")
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private fun launchChordsLibraryScreen() {
        composeTestRule.activity.setContent {
            navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "chords_library") {
                    composable("chords_library") {
                        ChordsLibraryScreen(
                            navController = navController,
                            currentRoute = "chords_library",
                            onNavigateToChordDetail = {},
                            onNavigateToPracticeCreation = {}
                        )
                    }
                }
            }
        }
    }

    @Test
    fun test_01_chordsLibrary_displays_search_and_filter() {
        (chordDao as FakeChordDao).setChords(testChords)
        launchChordsLibraryScreen()
        composeTestRule.onNodeWithTag(TestTagChordsLibrarySearchField).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTagChordsLibraryFilterButton).assertIsDisplayed()
    }

    @Test
    fun test_02_emptyState_displays_noChords_message() {
        (chordDao as FakeChordDao).setChords(emptyList())
        launchChordsLibraryScreen()
        composeTestRule.onNodeWithTag(TestTagChordsLibraryEmptyMessage).assertIsDisplayed()
    }

    @Test
    fun test_03_chordItems_displayed_with_correct_text() {
        (chordDao as FakeChordDao).setChords(testChords)
        launchChordsLibraryScreen()
        composeTestRule.onNodeWithTag("chord_item_1").assertIsDisplayed().assertTextContains("C major")
        composeTestRule.onNodeWithTag("chord_item_2").assertIsDisplayed().assertTextContains("G minor")
    }

    @Test
    fun test_04_searchField_accepts_input_and_filters() {
        (chordDao as FakeChordDao).setChords(testChords)
        launchChordsLibraryScreen()

        composeTestRule.onNodeWithTag(TestTagChordsLibrarySearchField).performTextInput("C major")
        composeTestRule.onNodeWithTag("chord_item_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chord_item_2").assertDoesNotExist()
    }

    @Test
    fun test_05_selection_shows_delete_and_practice_actions() {
        (chordDao as FakeChordDao).setChords(testChords)
        launchChordsLibraryScreen()

        composeTestRule.onNodeWithTag(TestTagChordsLibraryPracticeFab).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTagChordsLibraryDeleteButton).assertDoesNotExist()

        composeTestRule.onNodeWithTag("chord_checkbox_1").performClick()

        composeTestRule.onNodeWithTag(TestTagChordsLibraryPracticeFab).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTagChordsLibraryDeleteButton).assertIsDisplayed()
    }

    @Test
    fun test_06_multipleChords_are_scrollable() {
        (chordDao as FakeChordDao).setChords(testChords)
        launchChordsLibraryScreen()
        composeTestRule.onNodeWithTag(TestTagChordsLibraryLazyColumn).performScrollToNode(hasTestTag("chord_item_5"))
        composeTestRule.onNodeWithTag("chord_item_5").assertIsDisplayed()
    }
}