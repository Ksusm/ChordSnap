package cz.mendelu.pef.chordsnap.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChordType(
    @SerialName("_id")
    val id: String,

    val name: ChordName,
    val intervals: List<String>,
    val description: ChordName
)