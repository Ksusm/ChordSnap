package cz.mendelu.examtemplate2025.navigation

sealed class Destination(
    val route: String
){
    object MainScreen : Destination(route = "main")
}
