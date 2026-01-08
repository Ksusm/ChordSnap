package cz.mendelu.pef.chordsnap.ui.screens.chordslibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.pef.chordsnap.database.ChordDao
import cz.mendelu.pef.chordsnap.database.ChordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChordsLibraryUiState {
    object Loading : ChordsLibraryUiState()
    data class Success(val chords: List<ChordEntity>) : ChordsLibraryUiState()
    data class Error(val message: String) : ChordsLibraryUiState()
}

@HiltViewModel
class ChordsLibraryViewModel @Inject constructor(
    private val chordDao: ChordDao
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow<String?>(null)

    private val _selectedChords = MutableStateFlow<Set<String>>(emptySet())
    val selectedChords: StateFlow<Set<String>> = _selectedChords.asStateFlow()

    val uiState: StateFlow<ChordsLibraryUiState> = combine(
        chordDao.getAllChords(),
        _searchQuery,
        _filterType
    ) { chords, query, filterType ->
        try {
            var filteredChords = chords

            if (query.isNotBlank()) {
                filteredChords = filteredChords.filter { chord ->
                    chord.nameEng.contains(query, ignoreCase = true) ||
                            chord.nameSpa.contains(query, ignoreCase = true)
                }
            }

            if (filterType != null) {
                filteredChords = filteredChords.filter { chord ->
                    chord.typeId.contains(filterType, ignoreCase = true)
                }
            }

            ChordsLibraryUiState.Success(filteredChords)
        } catch (e: Exception) {
            ChordsLibraryUiState.Error("Failed to load chords: ${e.message}")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChordsLibraryUiState.Loading
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun filterByType(type: String?) {
        _filterType.value = type
    }

    fun toggleChordSelection(chordId: String, isSelected: Boolean) {
        _selectedChords.value = if (isSelected) {
            _selectedChords.value + chordId
        } else {
            _selectedChords.value - chordId
        }
    }

    fun deleteSelectedChords() {
        viewModelScope.launch {
            try {
                chordDao.deleteByIds(_selectedChords.value.toList())
                _selectedChords.value = emptySet()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}