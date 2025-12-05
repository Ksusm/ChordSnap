package cz.mendelu.examtemplate2025.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.examtemplate2025.models.Park
import cz.mendelu.examtemplate2025.notifications.NotificationHelper
import cz.mendelu.examtemplate2025.repository.ApiResult
import cz.mendelu.examtemplate2025.repository.ParksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: ParksRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainScreenUIState>(MainScreenUIState.Loading)
    val uiState: StateFlow<MainScreenUIState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {


        viewModelScope.launch {
            repository.getParks().collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _uiState.value = MainScreenUIState.Loading
                    }
                    is ApiResult.Success -> {
                        val sortedParks = result.data.sortedBy { it.name }
                        _uiState.value = MainScreenUIState.Success(sortedParks)
                    }
                    is ApiResult.Error -> {
                        _uiState.value = MainScreenUIState.Error(result.message)
                    }
                }
            }
        }
    }

    fun onParkClick(park: Park) {
        val title = "Park notification"
        val message = if (park.showTips) {
            "Park ${park.name}"
        } else {
            "PLACEHOLDER"
        }

        notificationHelper.showParkNotification(title, message)
    }

    fun retry() {
        loadData()
    }
}
