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
class PracticeViewScreenUITest {

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

    private val testPractice = PracticeEntity(1, "Test Practice", "1,2,3")

    @Before
    fun setup() {
        hiltRule.inject()
        (chordDao as FakeChordDao).setChords(testChords)
        (practiceDao as FakePracticeDao).setPractices(listOf(testPractice))
    }

    private fun launchPracticeViewScreen(practiceId: Long = 1) {
        composeRule.activity.setContent {
            navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "practice_view/$practiceId") {
                    composable("practice_view/{practiceId}") {
                        PracticeViewScreen(
                            practiceId = practiceId,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToEdit = {},
                            onNavigateToChordDetail = {}
                        )
                    }
                }
            }
        }
    }

    @Test
    fun test_01_practiceView_displays_all_elements() {
        launchPracticeViewScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeViewTitle).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagPracticeViewBackButton).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagPracticeViewEditButton).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagPracticeViewDeleteButton).assertIsDisplayed()
        composeRule.onNodeWithText("Test Practice").assertIsDisplayed()
    }

    @Test
    fun test_02_practiceView_displays_error_when_not_found() {
        (practiceDao as FakePracticeDao).setPractices(emptyList())
        launchPracticeViewScreen(999)
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeViewError).assertIsDisplayed()
    }

    @Test
    fun test_03_practiceView_displays_all_chords() {
        launchPracticeViewScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("practice_view_chord_1").assertIsDisplayed()
        composeRule.onNodeWithTag("practice_view_chord_2").assertIsDisplayed()
        composeRule.onNodeWithTag("practice_view_chord_3").assertIsDisplayed()
        composeRule.onNodeWithText("C major").assertIsDisplayed()
        composeRule.onNodeWithText("G minor").assertIsDisplayed()
        composeRule.onNodeWithText("D major").assertIsDisplayed()
    }

    @Test
    fun test_04_practiceView_edit_button_triggers_navigation() {
        var navigatedToEditId: Long? = null

        composeRule.activity.setContent {
            navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "practice_view/1") {
                    composable("practice_view/{practiceId}") {
                        PracticeViewScreen(
                            practiceId = 1,
                            onNavigateBack = {},
                            onNavigateToEdit = { navigatedToEditId = it },
                            onNavigateToChordDetail = {}
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(TestTagPracticeViewEditButton).performClick()
        composeRule.waitForIdle()

        assert(navigatedToEditId == 1L)
    }

    @Test
    fun test_05_practiceView_delete_dialog_appears() {
        launchPracticeViewScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeViewDeleteButton).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeViewDeleteDialogTitle).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagPracticeViewDeleteDialogMessage).assertIsDisplayed()
    }

    @Test
    fun test_06_practiceView_chord_click_navigates_to_detail() {
        var navigatedChordId: String? = null

        composeRule.activity.setContent {
            navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "practice_view/1") {
                    composable("practice_view/{practiceId}") {
                        PracticeViewScreen(
                            practiceId = 1,
                            onNavigateBack = {},
                            onNavigateToEdit = {},
                            onNavigateToChordDetail = { navigatedChordId = it }
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("practice_view_chord_1").performClick()
        composeRule.waitForIdle()

        assert(navigatedChordId == "1")
    }

    @Test
    fun test_07_practiceView_back_button_navigates() {
        launchPracticeViewScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeViewBackButton).performClick()
        composeRule.waitForIdle()

        assert(navController.currentBackStackEntry?.destination?.route != "practice_view/{practiceId}")
    }

    @Test
    fun test_08_practiceView_delete_confirms_and_navigates_back() {
        var navigatedBack = false

        composeRule.activity.setContent {
            navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "practice_view/1") {
                    composable("practice_view/{practiceId}") {
                        PracticeViewScreen(
                            practiceId = 1,
                            onNavigateBack = { navigatedBack = true },
                            onNavigateToEdit = {},
                            onNavigateToChordDetail = {}
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeViewDeleteButton).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeViewConfirmButton).performClick()
        composeRule.waitForIdle()

        assert(navigatedBack)

        val practices = runBlocking {
            (practiceDao as FakePracticeDao).getAllPractices().first()
        }
        assert(practices.isEmpty())
    }

    @Test
    fun test_09_practiceView_delete_dialog_cancel() {
        launchPracticeViewScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeViewDeleteButton).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeViewCancelButton).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeViewDeleteDialogTitle).assertDoesNotExist()

        val practices = runBlocking {
            (practiceDao as FakePracticeDao).getAllPractices().first()
        }
        assert(practices.size == 1)
    }

    @Test
    fun test_10_practiceView_chords_are_scrollable() {
        val manyChords = (1..10).map {
            ChordEntity("$it", "Chord $it", "Acorde $it", "C", "major", "url$it")
        }
        val largePractice = PracticeEntity(1, "Large Practice", manyChords.joinToString(",") { it.id })

        (chordDao as FakeChordDao).setChords(manyChords)
        (practiceDao as FakePracticeDao).setPractices(listOf(largePractice))

        launchPracticeViewScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagPracticeViewLazyColumn)
            .performScrollToNode(hasTestTag("practice_view_chord_10"))

        composeRule.onNodeWithTag("practice_view_chord_10").assertIsDisplayed()
    }
}