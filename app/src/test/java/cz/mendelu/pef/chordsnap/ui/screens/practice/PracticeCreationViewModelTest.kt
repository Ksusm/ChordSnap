package cz.mendelu.pef.chordsnap.ui.screens.practice

import cz.mendelu.pef.chordsnap.database.ChordDao
import cz.mendelu.pef.chordsnap.database.ChordEntity
import cz.mendelu.pef.chordsnap.database.PracticeDao
import cz.mendelu.pef.chordsnap.database.PracticeEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PracticeCreationViewModelTest {

    private lateinit var chordDao: ChordDao
    private lateinit var practiceDao: PracticeDao
    private lateinit var viewModel: PracticeCreationViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testChords = listOf(
        ChordEntity("1", "C major", "Do mayor", "C", "major", "url1"),
        ChordEntity("2", "G minor", "Sol menor", "G", "minor", "url2"),
        ChordEntity("3", "D major", "Re mayor", "D", "major", "url3")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        chordDao = mockk(relaxed = true)
        practiceDao = mockk(relaxed = true)

        coEvery { chordDao.getAllChords() } returns flowOf(testChords)

        viewModel = PracticeCreationViewModel(chordDao, practiceDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPractice with new practice loads selected chords`() = runTest {
        viewModel.loadPractice(null, listOf("1", "2"))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PracticeCreationUiState.Success)
        assertEquals(2, (state as PracticeCreationUiState.Success).chords.size)
        assertEquals("Practice", state.practiceName)
    }

    @Test
    fun `loadPractice loads existing practice`() = runTest {
        val testPractice = PracticeEntity(1, "My Practice", "1,2")
        coEvery { practiceDao.getPracticeById(1L) } returns testPractice

        viewModel.loadPractice(1L, emptyList())
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PracticeCreationUiState.Success)
        assertEquals("My Practice", (state as PracticeCreationUiState.Success).practiceName)
    }

    @Test
    fun `updatePracticeName updates name`() = runTest {
        viewModel.loadPractice(null, listOf("1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updatePracticeName("New Name")

        val state = viewModel.uiState.value
        assertTrue(state is PracticeCreationUiState.Success)
        assertEquals("New Name", (state as PracticeCreationUiState.Success).practiceName)
    }

    @Test
    fun `removeChord removes chord from list`() = runTest {
        viewModel.loadPractice(null, listOf("1", "2"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.removeChord("1")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PracticeCreationUiState.Success)
        assertEquals(1, (state as PracticeCreationUiState.Success).chords.size)
        assertEquals("2", state.chords[0].id)
    }

    @Test
    fun `addChord adds new chord to practice`() = runTest {
        viewModel.loadPractice(null, listOf("1"))
        testDispatcher.scheduler.advanceUntilIdle()

        val newChord = testChords[1]
        viewModel.addChord("2", newChord)

        val state = viewModel.uiState.value
        assertTrue(state is PracticeCreationUiState.Success)
        assertEquals(2, (state as PracticeCreationUiState.Success).chords.size)
    }

    @Test
    fun `moveChordUp moves chord correctly`() = runTest {
        viewModel.loadPractice(null, listOf("1", "2"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.moveChordUp(1)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PracticeCreationUiState.Success)
        assertEquals("2", (state as PracticeCreationUiState.Success).chords[0].id)
        assertEquals("1", state.chords[1].id)
    }

    @Test
    fun `moveChordDown swaps chords correctly`() = runTest {
        viewModel.loadPractice(null, listOf("1", "2"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.moveChordDown(0)

        val state = viewModel.uiState.value
        assertTrue(state is PracticeCreationUiState.Success)
        assertEquals("2", (state as PracticeCreationUiState.Success).chords[0].id)
        assertEquals("1", state.chords[1].id)
    }

    @Test
    fun `savePractice creates new practice`() = runTest {
        viewModel.loadPractice(null, listOf("1", "2"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updatePracticeName("Test Practice")
        testDispatcher.scheduler.advanceUntilIdle()

        val result = viewModel.savePractice()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result)
        coVerify { practiceDao.insertPractice(any()) }
    }

    @Test
    fun `savePractice updates existing practice`() = runTest {
        val practice = PracticeEntity(1, "Test", "1,2")
        coEvery { practiceDao.getPracticeById(1) } returns practice

        viewModel.loadPractice(1L, emptyList())
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.savePractice()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { practiceDao.updatePractice(any()) }
    }

    @Test
    fun `loadPractice with empty chord list creates empty practice`() = runTest {
        viewModel.loadPractice(null, emptyList())
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PracticeCreationUiState.Success)
        assertEquals(0, (state as PracticeCreationUiState.Success).chords.size)
    }

    @Test
    fun `addChord does not add duplicate chord`() = runTest {
        viewModel.loadPractice(null, listOf("1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addChord("1", testChords[0])

        val state = viewModel.uiState.value
        assertTrue(state is PracticeCreationUiState.Success)
        assertEquals(1, (state as PracticeCreationUiState.Success).chords.size)
    }

    @Test
    fun `moveChordUp at first position does nothing`() = runTest {
        viewModel.loadPractice(null, listOf("1", "2"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.moveChordUp(0)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PracticeCreationUiState.Success)
        assertEquals("1", (state as PracticeCreationUiState.Success).chords[0].id)
    }

    @Test
    fun `moveChordDown at last position does nothing`() = runTest {
        viewModel.loadPractice(null, listOf("1", "2"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.moveChordDown(1)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PracticeCreationUiState.Success)
        assertEquals("2", (state as PracticeCreationUiState.Success).chords[1].id)
    }
}