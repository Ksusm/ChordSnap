package cz.mendelu.pef.chordsnap.ui.screens.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.pef.chordsnap.communication.CommunicationResult
import cz.mendelu.pef.chordsnap.communication.IChordsRemoteRepository
import cz.mendelu.pef.chordsnap.database.ChordDao
import cz.mendelu.pef.chordsnap.database.toEntityList
import cz.mendelu.pef.chordsnap.models.Chord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ScanUiState {
    object Idle : ScanUiState()
    object Scanning : ScanUiState()
    object Processing : ScanUiState()
    data class Success(val chord: Chord) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

@HiltViewModel
class ScanScreenViewModel @Inject constructor(
    private val chordsRepository: IChordsRemoteRepository,
    private val chordDao: ChordDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private var allChords: List<Chord> = emptyList()

    init {
        loadAllChords()
    }

    private fun loadAllChords() {
        viewModelScope.launch {
            when (val result = chordsRepository.getAllChords()) {
                is CommunicationResult.Success -> {
                    allChords = result.data
                }
                else -> {
                    _uiState.value = ScanUiState.Error("Failed to load chord database")
                }
            }
        }
    }

    fun searchChordByName(recognizedText: String) {
        if (allChords.isEmpty()) {
            _uiState.value = ScanUiState.Error("Chord database not loaded yet")
            return
        }

        _uiState.value = ScanUiState.Processing

        viewModelScope.launch {
            val foundChord = findMatchingChord(allChords, recognizedText)

            if (foundChord != null) {
                saveChordToDatabase(foundChord)
                _uiState.value = ScanUiState.Success(foundChord)
            } else {
                _uiState.value = ScanUiState.Error("Chord '$recognizedText' not found in database.")
            }
        }
    }

    private fun findMatchingChord(chords: List<Chord>, recognizedText: String): Chord? {
        val cleanedText = recognizedText.trim()

        chords.forEach { chord ->
            if (chord.name.eng.equals(cleanedText, ignoreCase = true)) {
                return chord
            }
        }

        val normalizedText = normalizeChordName(cleanedText)
        chords.forEach { chord ->
            val normalizedChord = normalizeChordName(chord.name.eng)
            if (normalizedChord.equals(normalizedText, ignoreCase = true)) {
                return chord
            }
        }

        chords.forEach { chord ->
            val chordName = chord.name.eng.lowercase()
            val searchText = cleanedText.lowercase()

            if (searchText.contains(chordName) || chordName.contains(searchText)) {
                return chord
            }
        }

        return null
    }

    private fun normalizeChordName(name: String): String {
        var normalized = name.trim().replace(" ", "")

        normalized = normalized.replace(Regex("([A-G][b#]?)m$"), "$1 minor")
        normalized = normalized.replace(Regex("([A-G][b#]?)M$"), "$1 major")

        return normalized
    }

    private suspend fun saveChordToDatabase(chord: Chord) {
        try {
            chordDao.insertAll(listOf(chord).toEntityList())
        } catch (e: Exception) {
        }
    }

    fun resetState() {
        _uiState.value = ScanUiState.Idle
    }
}