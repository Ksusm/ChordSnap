package cz.mendelu.pef.chordsnap.ui.screens.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.pef.chordsnap.database.ChordDao
import cz.mendelu.pef.chordsnap.database.ChordEntity
import cz.mendelu.pef.chordsnap.database.PracticeDao
import cz.mendelu.pef.chordsnap.database.PracticeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PracticeViewUiState {
    object Loading : PracticeViewUiState()
    data class Success(
        val practice: PracticeEntity,
        val chords: List<ChordEntity>
    ) : PracticeViewUiState()
    data class Error(val message: String) : PracticeViewUiState()
}

@HiltViewModel
class PracticeViewViewModel @Inject constructor(
    private val chordDao: ChordDao,
    private val practiceDao: PracticeDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<PracticeViewUiState>(PracticeViewUiState.Loading)
    val uiState: StateFlow<PracticeViewUiState> = _uiState.asStateFlow()

    fun loadPractice(practiceId: Long) {
        viewModelScope.launch {
            try {
                val practice = practiceDao.getPracticeById(practiceId)
                if (practice != null) {
                    val chordIds = practice.chordIds.split(",").filter { it.isNotBlank() }
                    val allChords = chordDao.getAllChords().first()
                    val chords = chordIds.mapNotNull { id ->
                        allChords.find { it.id == id }
                    }

                    _uiState.value = PracticeViewUiState.Success(
                        practice = practice,
                        chords = chords
                    )
                } else {
                    _uiState.value = PracticeViewUiState.Error("Practice not found")
                }
            } catch (e: Exception) {
                _uiState.value = PracticeViewUiState.Error("Failed to load practice: ${e.message}")
            }
        }
    }

    fun deletePractice(practiceId: Long) {
        viewModelScope.launch {
            try {
                practiceDao.deletePracticeById(practiceId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}