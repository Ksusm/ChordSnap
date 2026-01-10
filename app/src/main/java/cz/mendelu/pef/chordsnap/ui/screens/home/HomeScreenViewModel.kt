package cz.mendelu.pef.chordsnap.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.pef.chordsnap.database.PracticeDao
import cz.mendelu.pef.chordsnap.database.PracticeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed class HomeScreenUiState {
    object Loading : HomeScreenUiState()
    data class Success(val practices: List<PracticeEntity>) : HomeScreenUiState()
    data class Error(val message: String) : HomeScreenUiState()
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val practiceDao: PracticeDao
) : ViewModel() {

    val uiState: StateFlow<HomeScreenUiState> = practiceDao.getAllPractices()
        .map<List<PracticeEntity>, HomeScreenUiState> { practices ->
            HomeScreenUiState.Success(practices)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeScreenUiState.Loading
        )
}