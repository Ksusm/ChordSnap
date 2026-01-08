package cz.mendelu.pef.chordsnap.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ChordEntity::class, PracticeEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chordDao(): ChordDao
    abstract fun practiceDao(): PracticeDao
}