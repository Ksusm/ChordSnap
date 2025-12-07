package cz.mendelu.pef.chordsnap.communication

data class CommunicationError(
    val code: Int,
    val message: String? = null
)