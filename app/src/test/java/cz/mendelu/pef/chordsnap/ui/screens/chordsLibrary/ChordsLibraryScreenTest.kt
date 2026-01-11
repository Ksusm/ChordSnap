package cz.mendelu.pef.chordsnap.ui.screens.chordslibrary

import app.cash.turbine.test
import cz.mendelu.pef.chordsnap.database.ChordDao
import cz.mendelu.pef.chordsnap.database.ChordEntity
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
class ChordsLibraryScreenTest {

    private lateinit var chordDao: ChordDao
    private lateinit var viewModel: ChordsLibraryViewModel
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
        coEvery { chordDao.getAllChords() } returns flowOf(testChords)
        viewModel = ChordsLibraryViewModel(chordDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search filters chords correctly`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)

            viewModel.updateSearchQuery("C major")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is ChordsLibraryUiState.Success, "Expected Success but was $state")
            val chords = (state as ChordsLibraryUiState.Success).chords
            assertEquals(1, chords.size)
            assertEquals("C major", chords[0].nameEng)
        }
    }

    @Test
    fun `filter by type works correctly`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)

            viewModel.filterByType("major")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is ChordsLibraryUiState.Success, "Expected Success but was $state")
            val chords = (state as ChordsLibraryUiState.Success).chords
            assertEquals(2, chords.size)
            assertTrue(chords.all { it.typeId == "major" })
        }
    }

    @Test
    fun `clearing filter shows all chords`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)

            viewModel.filterByType("major")
            testDispatcher.scheduler.advanceUntilIdle()
            val filteredState = awaitItem()
            assertEquals(2, (filteredState as ChordsLibraryUiState.Success).chords.size)

            viewModel.filterByType(null)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is ChordsLibraryUiState.Success, "Expected Success but was $state")
            assertEquals(3, (state as ChordsLibraryUiState.Success).chords.size)
        }
    }

    @Test
    fun `selection state persists across operations`() = runTest {
        viewModel.toggleChordSelection("1", true)
        viewModel.toggleChordSelection("2", true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectedChords.test {
            val initialSelected = awaitItem()
            assertTrue(initialSelected.contains("1"))

            viewModel.updateSearchQuery("C")
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.selectedChords.value.contains("1"))
        }
    }

    @Test
    fun `clearSelection removes all selections`() = runTest {
        viewModel.toggleChordSelection("1", true)
        viewModel.toggleChordSelection("2", true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectedChords.test {
            skipItems(1)

            viewModel.clearSelection()
            testDispatcher.scheduler.advanceUntilIdle()

            val selected = awaitItem()
            assertTrue(selected.isEmpty())
        }
    }

    @Test
    fun `deleteSelectedChords calls dao with correct ids`() = runTest {
        viewModel.toggleChordSelection("1", true)
        viewModel.toggleChordSelection("3", true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteSelectedChords()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { chordDao.deleteByIds(match { it.containsAll(listOf("1", "3")) }) }
    }
}