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
import cz.mendelu.pef.chordsnap.communication.IChordsRemoteRepository
import cz.mendelu.pef.chordsnap.fake.FakeChordsRemoteRepositoryImpl
import cz.mendelu.pef.chordsnap.models.*
import cz.mendelu.pef.chordsnap.ui.screens.chorddetail.*
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
class ChordDetailScreenUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var chordsRepository: IChordsRemoteRepository

    private lateinit var navController: NavHostController

    private val testChord = Chord(
        id = "1",
        noteId = "C",
        typeId = "major",
        notes = listOf("C", "E", "G"),
        name = ChordName("C major", "Do mayor"),
        images = ChordImages("url1")
    )

    private val testChordType = ChordType(
        id = "major",
        name = ChordName("Major chord", "Acorde mayor"),
        intervals = listOf("1", "3", "5"),
        description = ChordName("Major triad", "Tríada mayor")
    )

    private val testNote = Note(
        id = "C",
        name = ChordName("C", "Do"),
        type = "natural"
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private fun launchChordDetailScreen(chordId: String = "1") {
        composeRule.activity.setContent {
            navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "chord_detail/$chordId") {
                    composable("chord_detail/{chordId}") {
                        ChordDetailScreen(
                            chordId = chordId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    @Test
    fun test_01_chordDetail_displays_all_sections() {
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chords = listOf(testChord)
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chordTypes = listOf(testChordType)
        (chordsRepository as FakeChordsRemoteRepositoryImpl).notes = listOf(testNote)

        launchChordDetailScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("C major").assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagChordDetailDiagramTitle).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagChordDetailNotesTitle).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagChordDetailInfoTitle).assertIsDisplayed()
    }

    @Test
    fun test_02_chordDetail_displays_error_when_chord_not_found() {
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chords = emptyList()

        launchChordDetailScreen("999")
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagChordDetailError).assertIsDisplayed()
    }

    @Test
    fun test_03_chordDetail_displays_notes_card() {
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chords = listOf(testChord)
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chordTypes = listOf(testChordType)
        (chordsRepository as FakeChordsRemoteRepositoryImpl).notes = listOf(testNote)

        launchChordDetailScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagChordDetailNotesCard).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagChordDetailNotesTitle).assertIsDisplayed()
    }

    @Test
    fun test_04_chordDetail_displays_chord_type_and_base_note() {
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chords = listOf(testChord)
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chordTypes = listOf(testChordType)
        (chordsRepository as FakeChordsRemoteRepositoryImpl).notes = listOf(testNote)

        launchChordDetailScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagChordDetailChordTypeLabel).assertIsDisplayed()
        composeRule.onNodeWithText("Major chord").assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagChordDetailBaseNoteLabel).assertIsDisplayed()
    }

    @Test
    fun test_05_chordDetail_back_button_navigates() {
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chords = listOf(testChord)

        launchChordDetailScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagChordDetailBackButton).performClick()
        composeRule.waitForIdle()

        assert(navController.currentBackStackEntry?.destination?.route != "chord_detail/{chordId}")
    }

    @Test
    fun test_06_chordDetail_content_is_scrollable() {
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chords = listOf(testChord)
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chordTypes = listOf(testChordType)
        (chordsRepository as FakeChordsRemoteRepositoryImpl).notes = listOf(testNote)

        launchChordDetailScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagChordDetailDiagramTitle).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagChordDetailContent)
            .performScrollToNode(hasTestTag(TestTagChordDetailInfoTitle))
        composeRule.onNodeWithTag(TestTagChordDetailInfoTitle).assertIsDisplayed()
    }

    @Test
    fun test_07_chordDetail_displays_fallback_when_type_missing() {
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chords = listOf(testChord)
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chordTypes = emptyList()
        (chordsRepository as FakeChordsRemoteRepositoryImpl).notes = listOf(testNote)

        launchChordDetailScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagChordDetailChordTypeLabel).assertIsDisplayed()
        composeRule.onNodeWithText("major").assertIsDisplayed()
    }

    @Test
    fun test_08_chordDetail_displays_fallback_when_note_missing() {
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chords = listOf(testChord)
        (chordsRepository as FakeChordsRemoteRepositoryImpl).chordTypes = listOf(testChordType)
        (chordsRepository as FakeChordsRemoteRepositoryImpl).notes = emptyList()

        launchChordDetailScreen()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTagChordDetailBaseNoteLabel).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTagChordDetailInfoCard).assertIsDisplayed()

    }

    @Test
    fun test_09_chordDetail_back_button_triggers_callback() {
        var backCalled = false

        (chordsRepository as FakeChordsRemoteRepositoryImpl).chords = listOf(testChord)

        composeRule.activity.setContent {
            navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "chord_detail/1") {
                    composable("chord_detail/{chordId}") {
                        ChordDetailScreen(
                            chordId = "1",
                            onNavigateBack = { backCalled = true }
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(TestTagChordDetailBackButton).performClick()
        composeRule.waitForIdle()

        assert(backCalled)
    }
}