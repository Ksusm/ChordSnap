package cz.mendelu.pef.chordsnap.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chords")
data class ChordEntity(
    @PrimaryKey
    val id: String,
    val nameEng: String,
    val nameSpa: String,
    val noteId: String,
    val typeId: String,
    val imageUrl: String
)