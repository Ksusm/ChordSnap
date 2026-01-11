package cz.mendelu.pef.chordsnap.ui.screens.scan

import cz.mendelu.pef.chordsnap.R
import cz.mendelu.pef.chordsnap.communication.CommunicationResult
import cz.mendelu.pef.chordsnap.communication.IChordsRemoteRepository
import cz.mendelu.pef.chordsnap.database.ChordDao
import cz.mendelu.pef.chordsnap.models.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ScanScreenViewModelTest {

    private lateinit var repository: IChordsRemoteRepository
    private lateinit var chordDao: ChordDao
    private lateinit var viewModel: ScanScreenViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testChords = listOf(
        Chord(
            id = "1",
            noteId = "C",
            typeId = "major",
            notes = listOf("C", "E", "G"),
            name = ChordName("C major", "Do mayor"),
            images = ChordImages("url1")
        ),
        Chord(
            id = "2",
            noteId = "G",
            typeId = "minor",
            notes = listOf("G", "Bb", "D"),
            name = ChordName("G minor", "Sol menor"),
            images = ChordImages("url2")
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        chordDao = mockk(relaxed = true)

        coEvery { repository.getAllChords() } returns CommunicationResult.Success(testChords)
        coEvery { chordDao.insertAll(any()) } returns Unit

        viewModel = ScanScreenViewModel(repository, chordDao)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchChordByName finds exact match`() = runTest {
        viewModel.searchChordByName("C major")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ScanUiState.Success)
        assertEquals("1", (state as ScanUiState.Success).chord.id)
    }

    @Test
    fun `searchChordByName returns error for empty text`() = runTest {
        viewModel.searchChordByName("")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ScanUiState.Error)
        assertEquals(R.string.scan_no_chord_found, (state as ScanUiState.Error).messageResId)
    }

    @Test
    fun `searchChordByName normalizes single note to major`() = runTest {
        viewModel.searchChordByName("C")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ScanUiState.Success)
        assertEquals("C major", (state as ScanUiState.Success).chord.name.eng)
    }

    @Test
    fun `searchChordByName normalizes Cm to C minor`() = runTest {
        viewModel.searchChordByName("Gm")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ScanUiState.Success)
        assertEquals("G minor", (state as ScanUiState.Success).chord.name.eng)
    }

    @Test
    fun `searchChordByName returns error when chord not found`() = runTest {
        viewModel.searchChordByName("Z major")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ScanUiState.Error)
        assertEquals(R.string.scan_no_chord_found, (state as ScanUiState.Error).messageResId)
    }

    @Test
    fun `searchChordByName saves chord to database`() = runTest {
        viewModel.searchChordByName("C major")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { chordDao.insertAll(any()) }
    }

    @Test
    fun `resetState changes state to Idle`() = runTest {
        viewModel.searchChordByName("C major")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.resetState()

        val state = viewModel.uiState.value
        assertTrue(state is ScanUiState.Idle)
    }
}