package cz.mendelu.pef.chordsnap.ui.screens.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import cz.mendelu.pef.chordsnap.models.MusicPlace
import cz.mendelu.pef.chordsnap.models.MusicPlacesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class MapUiState(
    val musicPlaces: List<MusicPlace> = emptyList(),
    val selectedPlace: MusicPlace? = null,
    val cameraPosition: LatLng = LatLng(49.1951, 16.6068),
    val isLoading: Boolean = false
)

@HiltViewModel
class MapScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadMusicPlaces()
    }

    private fun loadMusicPlaces() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val jsonString = context.assets
                    .open("music_places.json")
                    .bufferedReader()
                    .use { it.readText() }

                val response = json.decodeFromString<MusicPlacesResponse>(jsonString)

                _uiState.value = _uiState.value.copy(
                    musicPlaces = response.places,
                    isLoading = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onPlaceSelected(place: MusicPlace?) {
        _uiState.value = _uiState.value.copy(selectedPlace = place)
    }

    fun dismissBottomSheet() {
        _uiState.value = _uiState.value.copy(selectedPlace = null)
    }
}