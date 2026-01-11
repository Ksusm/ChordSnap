package cz.mendelu.pef.chordsnap.ui.screens.chorddetail

import cz.mendelu.pef.chordsnap.communication.CommunicationResult
import cz.mendelu.pef.chordsnap.communication.IChordsRemoteRepository
import cz.mendelu.pef.chordsnap.models.*
import io.mockk.coEvery
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
class ChordDetailViewModelTest {

    private lateinit var repository: IChordsRemoteRepository
    private lateinit var viewModel: ChordDetailViewModel
    private val testDispatcher = StandardTestDispatcher()

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
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        coEvery { repository.getAllChords() } returns CommunicationResult.Success(listOf(testChord))
        coEvery { repository.getChordTypes() } returns CommunicationResult.Success(listOf(testChordType))
        coEvery { repository.getAllNotes() } returns CommunicationResult.Success(listOf(testNote))

        viewModel = ChordDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadChord loads chord with additional info`() = runTest {
        viewModel.loadChord("1")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ChordDetailUiState.Success)
        assertEquals("C major", (state as ChordDetailUiState.Success).chord.name.eng)
        assertEquals("Major chord", state.chordType?.name?.eng)
        assertEquals("C", state.baseNote?.name?.eng)
    }

    @Test
    fun `loadChord returns error when chord not found`() = runTest {
        viewModel.loadChord("999")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ChordDetailUiState.Error)
        assertEquals("Chord not found", (state as ChordDetailUiState.Error).message)
    }

    @Test
    fun `loadChord returns error on API failure`() = runTest {
        coEvery { repository.getAllChords() } returns CommunicationResult.ConnectionError()

        viewModel.loadChord("1")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ChordDetailUiState.Error)
        assertEquals("Connection error", (state as ChordDetailUiState.Error).message)
    }

    @Test
    fun `loadChord loads chord even when types fail`() = runTest {
        coEvery { repository.getChordTypes() } returns CommunicationResult.Error(
            cz.mendelu.pef.chordsnap.communication.CommunicationError(500)
        )

        viewModel.loadChord("1")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ChordDetailUiState.Success)
        assertEquals("C major", (state as ChordDetailUiState.Success).chord.name.eng)
        assertEquals(null, state.chordType)
    }
}