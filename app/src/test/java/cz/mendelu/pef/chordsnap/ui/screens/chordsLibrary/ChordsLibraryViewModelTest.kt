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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ChordsLibraryViewModelTest {

    private lateinit var chordDao: ChordDao
    private lateinit var viewModel: ChordsLibraryViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testChords = listOf(
        ChordEntity("1", "C major", "Do mayor", "C", "major", "url1"),
        ChordEntity("2", "G minor", "Sol menor", "G", "minor", "url2"),
        ChordEntity("3", "D major", "Re mayor", "D", "major", "url3"),
        ChordEntity("4", "A diminished", "La disminuido", "A", "diminished", "url4"),
        ChordEntity("5", "E7", "Mi7", "E", "7", "url5")
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
    fun `uiState emits Success with all chords when no filter`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val item = awaitItem()
            val state = if (item is ChordsLibraryUiState.Loading) awaitItem() else item

            assertTrue(state is ChordsLibraryUiState.Success, "Expected Success but was $state")
            assertEquals(5, (state as ChordsLibraryUiState.Success).chords.size)
        }
    }

    @Test
    fun `search filters chords by name`() = runTest {
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
    fun `filterByType filters chords correctly`() = runTest {
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
    fun `toggleChordSelection adds chord to selection`() = runTest {
        viewModel.toggleChordSelection("1", true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectedChords.test {
            val selected = awaitItem()
            assertTrue(selected.contains("1"))
        }
    }

    @Test
    fun `toggleChordSelection removes chord from selection`() = runTest {
        viewModel.toggleChordSelection("1", true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleChordSelection("1", false)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectedChords.test {
            val selected = awaitItem()
            assertTrue(!selected.contains("1"))
        }
    }

    @Test
    fun `deleteSelectedChords calls DAO with selected IDs`() = runTest {
        viewModel.toggleChordSelection("1", true)
        viewModel.toggleChordSelection("2", true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteSelectedChords()

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { chordDao.deleteByIds(listOf("1", "2")) }
    }

    @Test
    fun `search filters chords case insensitively`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)

            viewModel.updateSearchQuery("g MINOR")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is ChordsLibraryUiState.Success)
            val chords = (state as ChordsLibraryUiState.Success).chords
            assertEquals(1, chords.size)
            assertEquals("G minor", chords[0].nameEng)
        }
    }

    @Test
    fun `clearSelection removes all selections`() = runTest {
        viewModel.toggleChordSelection("1", true)
        viewModel.toggleChordSelection("2", true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearSelection()

        viewModel.selectedChords.test {
            val selected = awaitItem()
            assertTrue(selected.isEmpty())
        }
    }

    @Test
    fun `deleteSelectedChords clears selection after deletion`() = runTest {
        viewModel.toggleChordSelection("1", true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteSelectedChords()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectedChords.test {
            val selected = awaitItem()
            assertTrue(selected.isEmpty())
        }
    }

    @Test
    fun `search with empty query shows all chords`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)

            viewModel.updateSearchQuery("")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is ChordsLibraryUiState.Success)
            assertEquals(5, (state as ChordsLibraryUiState.Success).chords.size)
        }
    }

    @Test
    fun `filterByType filters diminished chords correctly`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)

            viewModel.filterByType("diminished")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is ChordsLibraryUiState.Success)
            val chords = (state as ChordsLibraryUiState.Success).chords
            assertEquals(1, chords.size)
            assertEquals("diminished", chords[0].typeId)
        }
    }

    @Test
    fun `filterByType filters 7th chords correctly`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)

            viewModel.filterByType("7")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is ChordsLibraryUiState.Success)
            val chords = (state as ChordsLibraryUiState.Success).chords
            assertEquals(1, chords.size)
            assertEquals("7", chords[0].typeId)
        }
    }

    @Test
    fun `search and filter work together`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)

            viewModel.updateSearchQuery("major")
            viewModel.filterByType("major")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is ChordsLibraryUiState.Success)
            val chords = (state as ChordsLibraryUiState.Success).chords
            assertEquals(2, chords.size)
            assertTrue(chords.all { it.typeId == "major" })
        }
    }
}