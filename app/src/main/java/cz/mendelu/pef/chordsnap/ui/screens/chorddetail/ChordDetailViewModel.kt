package cz.mendelu.pef.chordsnap.ui.screens.chorddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.pef.chordsnap.communication.CommunicationResult
import cz.mendelu.pef.chordsnap.communication.IChordsRemoteRepository
import cz.mendelu.pef.chordsnap.models.Chord
import cz.mendelu.pef.chordsnap.models.ChordType
import cz.mendelu.pef.chordsnap.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChordDetailUiState {
    object Loading : ChordDetailUiState()
    data class Success(
        val chord: Chord,
        val chordType: ChordType? = null,
        val baseNote: Note? = null
    ) : ChordDetailUiState()
    data class Error(val message: String) : ChordDetailUiState()
}

@HiltViewModel
class ChordDetailViewModel @Inject constructor(
    private val chordsRepository: IChordsRemoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChordDetailUiState>(ChordDetailUiState.Loading)
    val uiState: StateFlow<ChordDetailUiState> = _uiState.asStateFlow()

    fun loadChord(chordId: String) {
        _uiState.value = ChordDetailUiState.Loading

        viewModelScope.launch {
            when (val chordsResult = chordsRepository.getAllChords()) {
                is CommunicationResult.Success -> {
                    val chord = chordsResult.data.find { it.id == chordId }

                    if (chord != null) {
                        loadAdditionalInfo(chord)
                    } else {
                        _uiState.value = ChordDetailUiState.Error("Chord not found")
                    }
                }
                is CommunicationResult.Error -> {
                    _uiState.value = ChordDetailUiState.Error("Failed to load chord")
                }
                is CommunicationResult.ConnectionError -> {
                    _uiState.value = ChordDetailUiState.Error("Connection error")
                }
                is CommunicationResult.Exception -> {
                    _uiState.value = ChordDetailUiState.Error("Error: ${chordsResult.exception.message}")
                }
            }
        }
    }

    private suspend fun loadAdditionalInfo(chord: Chord) {
        var chordType: ChordType? = null
        var baseNote: Note? = null

        when (val typesResult = chordsRepository.getChordTypes()) {
            is CommunicationResult.Success -> {
                chordType = typesResult.data.find { it.id == chord.typeId }
            }
            else -> {}
        }

        when (val notesResult = chordsRepository.getAllNotes()) {
            is CommunicationResult.Success -> {
                baseNote = notesResult.data.find { it.id == chord.noteId }
            }
            else -> {}
        }

        _uiState.value = ChordDetailUiState.Success(
            chord = chord,
            chordType = chordType,
            baseNote = baseNote
        )
    }
}