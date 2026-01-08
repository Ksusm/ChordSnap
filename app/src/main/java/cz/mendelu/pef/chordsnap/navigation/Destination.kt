package cz.mendelu.pef.chordsnap.navigation

sealed class Destination(
    val route: String
) {
    object HomeScreen : Destination(route = "home")
    object ScanScreen : Destination(route = "scan")
    object ChordsLibraryScreen : Destination(route = "chords_library")
    object ChordDetailScreen : Destination(route = "chord_detail/{chordId}") {
        fun createRoute(chordId: String) = "chord_detail/$chordId"
    }
    object PracticeCreationScreen : Destination(route = "practice_creation?practiceId={practiceId}&chordIds={chordIds}") {
        fun createRoute(practiceId: Long? = null, chordIds: String = "") =
            "practice_creation?practiceId=$practiceId&chordIds=$chordIds"
    }
    object PracticeViewScreen : Destination(route = "practice_view/{practiceId}") {
        fun createRoute(practiceId: Long) = "practice_view/$practiceId"
    }
    object MapScreen : Destination(route = "map")
    object SettingsScreen : Destination(route = "settings")
}