package cz.mendelu.pef.chordsnap.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    @SerialName("_id")
    val id: String,

    val name: ChordName,
    val type: String
)