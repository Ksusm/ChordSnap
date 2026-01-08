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

sealed class PracticeCreationUiState {
    object Loading : PracticeCreationUiState()
    data class Success(
        val chords: List<ChordEntity>,
        val practiceName: String
    ) : PracticeCreationUiState()
    data class Error(val message: String) : PracticeCreationUiState()
}

@HiltViewModel
class PracticeCreationViewModel @Inject constructor(
    private val chordDao: ChordDao,
    private val practiceDao: PracticeDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<PracticeCreationUiState>(PracticeCreationUiState.Loading)
    val uiState: StateFlow<PracticeCreationUiState> = _uiState.asStateFlow()

    private var selectedChordIds: List<String> = emptyList()
    private var practiceId: Long? = null

    fun loadPractice(practiceIdParam: Long?, selectedIds: List<String>) {
        viewModelScope.launch {
            try {
                practiceId = practiceIdParam

                if (practiceId != null) {
                    val practice = practiceDao.getPracticeById(practiceId!!)
                    if (practice != null) {
                        selectedChordIds = practice.chordIds.split(",").filter { it.isNotBlank() }
                        loadChords(practice.name)
                    } else {
                        _uiState.value = PracticeCreationUiState.Error("Practice not found")
                    }
                } else {
                    selectedChordIds = selectedIds
                    loadChords("Practice")
                }
            } catch (e: Exception) {
                _uiState.value = PracticeCreationUiState.Error("Failed to load practice: ${e.message}")
            }
        }
    }

    private suspend fun loadChords(practiceName: String) {
        val allChords = chordDao.getAllChords().first()
        val selectedChords = selectedChordIds.mapNotNull { id ->
            allChords.find { it.id == id }
        }

        _uiState.value = PracticeCreationUiState.Success(
            chords = selectedChords,
            practiceName = practiceName
        )
    }

    fun updatePracticeName(name: String) {
        val currentState = _uiState.value
        if (currentState is PracticeCreationUiState.Success) {
            _uiState.value = currentState.copy(practiceName = name)
        }
    }

    fun removeChord(chordId: String) {
        selectedChordIds = selectedChordIds.filter { it != chordId }
        val currentState = _uiState.value
        if (currentState is PracticeCreationUiState.Success) {
            val updatedChords = currentState.chords.filter { it.id != chordId }
            _uiState.value = currentState.copy(chords = updatedChords)
        }
    }

    fun moveChordUp(index: Int) {
        if (index > 0) {
            selectedChordIds = selectedChordIds.toMutableList().apply {
                val temp = this[index]
                this[index] = this[index - 1]
                this[index - 1] = temp
            }

            val currentState = _uiState.value
            if (currentState is PracticeCreationUiState.Success) {
                val updatedChords = currentState.chords.toMutableList().apply {
                    val temp = this[index]
                    this[index] = this[index - 1]
                    this[index - 1] = temp
                }
                _uiState.value = currentState.copy(chords = updatedChords)
            }
        }
    }

    fun moveChordDown(index: Int) {
        val currentState = _uiState.value
        if (currentState is PracticeCreationUiState.Success && index < currentState.chords.size - 1) {
            selectedChordIds = selectedChordIds.toMutableList().apply {
                val temp = this[index]
                this[index] = this[index + 1]
                this[index + 1] = temp
            }

            val updatedChords = currentState.chords.toMutableList().apply {
                val temp = this[index]
                this[index] = this[index + 1]
                this[index + 1] = temp
            }
            _uiState.value = currentState.copy(chords = updatedChords)
        }
    }

    suspend fun savePractice(): Boolean {
        val currentState = _uiState.value
        if (currentState !is PracticeCreationUiState.Success) return false

        return try {
            val practice = PracticeEntity(
                id = practiceId ?: 0,
                name = currentState.practiceName,
                chordIds = selectedChordIds.joinToString(",")
            )

            if (practiceId != null) {
                practiceDao.updatePractice(practice)
            } else {
                practiceDao.insertPractice(practice)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}