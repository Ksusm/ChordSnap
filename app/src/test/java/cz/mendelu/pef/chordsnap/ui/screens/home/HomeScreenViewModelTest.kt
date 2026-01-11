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
class HomeScreenViewModelTest {

    private lateinit var practiceDao: PracticeDao
    private lateinit var viewModel: HomeScreenViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testPractices = listOf(
        PracticeEntity(1, "Strumming basics", "1,2,3"),
        PracticeEntity(2, "Moon river", "4,5,6,7,8")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        practiceDao = mockk(relaxed = true)
        coEvery { practiceDao.getAllPractices() } returns flowOf(testPractices)
        viewModel = HomeScreenViewModel(practiceDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState emits Success with practices`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val item = awaitItem()
            val state = if (item is HomeScreenUiState.Loading) awaitItem() else item

            assertTrue(state is HomeScreenUiState.Success)
            assertEquals(2, (state as HomeScreenUiState.Success).practices.size)
            assertEquals("Strumming basics", state.practices[0].name)
        }
    }

    @Test
    fun `uiState emits empty list when no practices`() = runTest {
        coEvery { practiceDao.getAllPractices() } returns flowOf(emptyList())
        val emptyViewModel = HomeScreenViewModel(practiceDao)
        testDispatcher.scheduler.advanceUntilIdle()

        emptyViewModel.uiState.test {
            val item = awaitItem()
            val state = if (item is HomeScreenUiState.Loading) awaitItem() else item

            assertTrue(state is HomeScreenUiState.Success)
            assertEquals(0, (state as HomeScreenUiState.Success).practices.size)
        }
    }
}