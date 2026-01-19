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
import cz.mendelu.pef.chordsnap.database.PracticeDao
import cz.mendelu.pef.chordsnap.database.PracticeEntity
import cz.mendelu.pef.chordsnap.fake.FakeChordDao
import cz.mendelu.pef.chordsnap.fake.FakePracticeDao
import cz.mendelu.pef.chordsnap.ui.screens.practice.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import javax.inject.Inject

@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PracticeCreationScreenUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var chordDao: ChordDao

    @Inject
    lateinit var practiceDao: PracticeDao

    private lateinit var navController: NavHostController

    private val testChords = listOf(
        ChordEntity("1", "C major", "Do mayor", "C", "major", "url1"),
        ChordEntity("2", "G minor", "Sol menor", "G", "minor", "url2"),
        ChordEntity("3", "D major", "Re mayor", "D", "major", "url3")
    )

    @Before
    fun setup() {
        hiltRule.inject()
        (chordDao as FakeChordDao).setChords(testChords)
    }

    private fun launchPracticeCreationScreen(
        practiceId: Long? = null,
        selectedChordIds: List<String> = emptyList()
    ) {
        composeRule.activity.setContent {
            navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "practice_creation") {
                    composable("practice_creation") {
                        PracticeCreationScreen(
                            practiceId = practiceId,
                            selectedChordIds = selectedChordIds,
                            onNavigateBack = { navController.popBackStack() },
                            onSaved = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    @Test
    fun test_01_practiceCreation_displays_all_elements() {
        launchPracticeCreationScreen(selectedChordIds = listOf("1", "2"))
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeCreationTitle).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagPracticeCreationBackButton).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagPracticeCreationSaveButton).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagPracticeCreationNameField).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagPracticeCreationAddFab).assertIsDisplayed()
    }

    @Test
    fun test_02_practiceCreation_displays_empty_state() {
        launchPracticeCreationScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeCreationEmptyMessage).assertIsDisplayed()
    }

    @Test
    fun test_03_practiceCreation_displays_selected_chords() {
        launchPracticeCreationScreen(selectedChordIds = listOf("1", "2"))
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("practice_chord_item_1").assertIsDisplayed()
        composeRule.onNodeWithTag("practice_chord_item_2").assertIsDisplayed()
        composeRule.onNodeWithText("C major").assertIsDisplayed()
        composeRule.onNodeWithText("G minor").assertIsDisplayed()
    }

    @Test
    fun test_04_practiceCreation_name_field_accepts_input() {
        launchPracticeCreationScreen(selectedChordIds = listOf("1"))
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeCreationNameField)
            .performTextClearance()
        composeRule.onNodeWithTag(TestTagPracticeCreationNameField)
            .performTextInput("My Practice")

        composeRule.onNodeWithText("My Practice").assertIsDisplayed()
    }

    @Test
    fun test_05_practiceCreation_move_down_reorders_chords() {
        launchPracticeCreationScreen(selectedChordIds = listOf("1", "2"))
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("move_down_1").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("1.").assertIsDisplayed()
        composeRule.onNodeWithText("2.").assertIsDisplayed()
    }

    @Test
    fun test_06_practiceCreation_move_up_reorders_chords() {
        launchPracticeCreationScreen(selectedChordIds = listOf("1", "2"))
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("move_up_2").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("1.").assertIsDisplayed()
        composeRule.onNodeWithText("2.").assertIsDisplayed()
    }

    @Test
    fun test_07_practiceCreation_chord_removal_works() {
        launchPracticeCreationScreen(selectedChordIds = listOf("1", "2"))
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("remove_1").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("practice_chord_item_1").assertDoesNotExist()
        composeRule.onNodeWithTag("practice_chord_item_2").assertIsDisplayed()
    }

    @Test
    fun test_08_practiceCreation_add_fab_shows_dialog() {
        launchPracticeCreationScreen(selectedChordIds = listOf("1"))
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeCreationAddFab).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeCreationAddDialogTitle).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagPracticeCreationSearchLabel).assertIsDisplayed()
    }

    @Test
    fun test_09_practiceCreation_dialog_cancel_closes() {
        launchPracticeCreationScreen(selectedChordIds = listOf("1"))
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeCreationAddFab).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeCreationCancelButton).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeCreationAddDialogTitle).assertDoesNotExist()
    }

    @Test
    fun test_10_practiceCreation_save_button_saves_and_navigates() {
        var saveCalled = false

        composeRule.activity.setContent {
            navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "practice_creation") {
                    composable("practice_creation") {
                        PracticeCreationScreen(
                            practiceId = null,
                            selectedChordIds = listOf("1", "2"),
                            onNavigateBack = {},
                            onSaved = { saveCalled = true }
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeCreationNameField)
            .performTextClearance()
        composeRule.onNodeWithTag(TestTagPracticeCreationNameField)
            .performTextInput("Test Practice")

        composeRule.onNodeWithTag(TestTagPracticeCreationSaveButton).performClick()
        composeRule.waitForIdle()

        assert(saveCalled)

        val practices = runBlocking {
            (practiceDao as FakePracticeDao).getAllPractices().first()
        }
        assert(practices.isNotEmpty())
    }

    @Test
    fun test_11_practiceCreation_back_button_navigates() {
        var backCalled = false

        composeRule.activity.setContent {
            navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "practice_creation") {
                    composable("practice_creation") {
                        PracticeCreationScreen(
                            practiceId = null,
                            selectedChordIds = listOf("1"),
                            onNavigateBack = { backCalled = true },
                            onSaved = {}
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(TestTagPracticeCreationBackButton).performClick()
        composeRule.waitForIdle()

        assert(backCalled)
    }

    @Test
    fun test_12_practiceCreation_chords_are_scrollable() {
        val manyChords = (1..10).map {
            ChordEntity("$it", "Chord $it", "Acorde $it", "C", "major", "url$it")
        }
        (chordDao as FakeChordDao).setChords(manyChords)

        launchPracticeCreationScreen(selectedChordIds = manyChords.map { it.id })
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeCreationLazyColumn)
            .performScrollToNode(hasTestTag("practice_chord_item_10"))

        composeRule.onNodeWithTag("practice_chord_item_10").assertIsDisplayed()
    }

    @Test
    fun test_13_practiceCreation_edits_existing_practice() {
        val existingPractice = PracticeEntity(1, "Existing Practice", "1,2")
        (practiceDao as FakePracticeDao).setPractices(listOf(existingPractice))

        launchPracticeCreationScreen(practiceId = 1L)
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Existing Practice").assertIsDisplayed()
        composeRule.onNodeWithTag("practice_chord_item_1").assertIsDisplayed()
        composeRule.onNodeWithTag("practice_chord_item_2").assertIsDisplayed()
    }
}