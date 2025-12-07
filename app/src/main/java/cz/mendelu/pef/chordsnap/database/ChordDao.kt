package cz.mendelu.pef.chordsnap.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChordDao {

    @Query("SELECT * FROM chords")
    fun getAllChords(): Flow<List<ChordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chords: List<ChordEntity>)

    @Query("SELECT COUNT(*) FROM chords")
    suspend fun count(): Int
}