package cz.mendelu.pef.chordsnap.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Chord(
    @SerialName("_id")
    val id: String,

    val noteId: String,
    val typeId: String,
    val notes: List<String>,
    val name: ChordName,
    val images: ChordImages
)

@Serializable
data class ChordName(
    val eng: String,
    val spa: String
)

@Serializable
data class ChordImages(
    val pos1: String
)