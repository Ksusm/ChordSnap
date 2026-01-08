package cz.mendelu.pef.chordsnap.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeDao {

    @Query("SELECT * FROM practices ORDER BY id DESC")
    fun getAllPractices(): Flow<List<PracticeEntity>>

    @Query("SELECT * FROM practices WHERE id = :practiceId")
    suspend fun getPracticeById(practiceId: Long): PracticeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPractice(practice: PracticeEntity): Long

    @Update
    suspend fun updatePractice(practice: PracticeEntity)

    @Delete
    suspend fun deletePractice(practice: PracticeEntity)

    @Query("DELETE FROM practices WHERE id = :practiceId")
    suspend fun deletePracticeById(practiceId: Long)
}