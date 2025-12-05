package cz.mendelu.examtemplate2025.ui.screens

import cz.mendelu.examtemplate2025.models.Park

sealed class MainScreenUIState {
    object Loading : MainScreenUIState()
    data class Success(val parks: List<Park>) : MainScreenUIState()
    data class Error(val message: String) : MainScreenUIState()
}