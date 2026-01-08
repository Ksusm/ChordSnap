package cz.mendelu.pef.chordsnap.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practices")
data class PracticeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val chordIds: String
)