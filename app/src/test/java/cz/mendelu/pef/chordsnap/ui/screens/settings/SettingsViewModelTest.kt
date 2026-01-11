package cz.mendelu.pef.chordsnap.ui.screens.settings

import app.cash.turbine.test
import cz.mendelu.pef.chordsnap.datastore.UserPreferencesManager
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

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var preferencesManager: UserPreferencesManager
    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        preferencesManager = mockk(relaxed = true)

        coEvery { preferencesManager.languageFlow } returns flowOf("en")
        coEvery { preferencesManager.themeFlow } returns flowOf("system")

        viewModel = SettingsViewModel(preferencesManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `language flow emits default value`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.language.test {
            assertEquals("en", awaitItem())
        }
    }

    @Test
    fun `theme flow emits default value`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.theme.test {
            assertEquals("system", awaitItem())
        }
    }

    @Test
    fun `setLanguage calls preferences manager`() = runTest {
        viewModel.setLanguage("cs")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { preferencesManager.setLanguage("cs") }
    }

    @Test
    fun `setTheme calls preferences manager`() = runTest {
        viewModel.setTheme("dark")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { preferencesManager.setTheme("dark") }
    }
}