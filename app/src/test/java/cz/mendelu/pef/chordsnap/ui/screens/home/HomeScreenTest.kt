package cz.mendelu.pef.chordsnap.ui.screens.home

import app.cash.turbine.test
import cz.mendelu.pef.chordsnap.database.PracticeDao
import cz.mendelu.pef.chordsnap.database.PracticeEntity
import io.mockk.coEvery
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
class HomeScreenTest {

    private lateinit var practiceDao: PracticeDao
    private lateinit var viewModel: HomeScreenViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        practiceDao = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel loads practices from dao`() = runTest {
        // Given
        val practices = listOf(
            PracticeEntity(1, "Strumming basics", "1,2,3"),
            PracticeEntity(2, "Moon river", "4,5,6")
        )
        coEvery { practiceDao.getAllPractices() } returns flowOf(practices)

        // When
        viewModel = HomeScreenViewModel(practiceDao)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val firstItem = awaitItem()
            val state = if (firstItem is HomeScreenUiState.Loading) awaitItem() else firstItem

            assertTrue(state is HomeScreenUiState.Success, "Expected Success but was $state")
            assertEquals(2, (state as HomeScreenUiState.Success).practices.size)
        }
    }

    @Test
    fun `viewModel handles empty practices list`() = runTest {
        // Given
        coEvery { practiceDao.getAllPractices() } returns flowOf(emptyList())

        // When
        viewModel = HomeScreenViewModel(practiceDao)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val firstItem = awaitItem()
            val state = if (firstItem is HomeScreenUiState.Loading) awaitItem() else firstItem

            assertTrue(state is HomeScreenUiState.Success, "Expected Success but was $state")
            assertEquals(0, (state as HomeScreenUiState.Success).practices.size)
        }
    }

    @Test
    fun `viewModel maintains practice order`() = runTest {
        // Given
        val practices = listOf(
            PracticeEntity(1, "First", "1"),
            PracticeEntity(2, "Second", "2"),
            PracticeEntity(3, "Third", "3")
        )
        coEvery { practiceDao.getAllPractices() } returns flowOf(practices)

        // When
        viewModel = HomeScreenViewModel(practiceDao)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val firstItem = awaitItem()
            val state = if (firstItem is HomeScreenUiState.Loading) awaitItem() else firstItem

            assertTrue(state is HomeScreenUiState.Success, "Expected Success but was $state")
            val loadedPractices = (state as HomeScreenUiState.Success).practices
            assertEquals("First", loadedPractices[0].name)
            assertEquals("Second", loadedPractices[1].name)
            assertEquals("Third", loadedPractices[2].name)
        }
    }
}