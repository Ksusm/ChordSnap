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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PracticeViewViewModelTest {

    private lateinit var chordDao: ChordDao
    private lateinit var practiceDao: PracticeDao
    private lateinit var viewModel: PracticeViewViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testChords = listOf(
        ChordEntity("1", "C major", "Do mayor", "C", "major", "url1"),
        ChordEntity("2", "G minor", "Sol menor", "G", "minor", "url2")
    )

    private val testPractice = PracticeEntity(1, "Test Practice", "1,2")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        chordDao = mockk(relaxed = true)
        practiceDao = mockk(relaxed = true)

        coEvery { chordDao.getAllChords() } returns flowOf(testChords)
        coEvery { practiceDao.getPracticeById(1L) } returns testPractice

        viewModel = PracticeViewViewModel(chordDao, practiceDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPractice loads practice with chords`() = runTest {
        viewModel.loadPractice(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PracticeViewUiState.Success)
        assertEquals("Test Practice", (state as PracticeViewUiState.Success).practice.name)
        assertEquals(2, state.chords.size)
    }

    @Test
    fun `loadPractice returns error when practice not found`() = runTest {
        coEvery { practiceDao.getPracticeById(999L) } returns null

        viewModel.loadPractice(999L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PracticeViewUiState.Error)
        assertEquals("Practice not found", (state as PracticeViewUiState.Error).message)
    }

    @Test
    fun `loadPractice handles empty chord IDs`() = runTest {
        val emptyPractice = PracticeEntity(2, "Empty", "")
        coEvery { practiceDao.getPracticeById(2L) } returns emptyPractice

        viewModel.loadPractice(2L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PracticeViewUiState.Success)
        assertEquals(0, (state as PracticeViewUiState.Success).chords.size)
    }

    @Test
    fun `deletePractice calls DAO`() = runTest {
        viewModel.deletePractice(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { practiceDao.deletePracticeById(1L) }
    }
}