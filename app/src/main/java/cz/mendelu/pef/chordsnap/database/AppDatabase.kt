package cz.mendelu.pef.chordsnap.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ChordEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chordDao(): ChordDao
}