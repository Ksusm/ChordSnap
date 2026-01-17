package cz.mendelu.pef.chordsnap.fake

import cz.mendelu.pef.chordsnap.database.ChordDao
import cz.mendelu.pef.chordsnap.database.ChordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeChordDao : ChordDao {

    private val chords = MutableStateFlow<List<ChordEntity>>(emptyList())

    override fun getAllChords(): Flow<List<ChordEntity>> {
        return chords
    }

    override suspend fun insertAll(chords: List<ChordEntity>) {
        this.chords.value = this.chords.value + chords
    }

    override suspend fun count(): Int {
        return chords.value.size
    }

    override suspend fun deleteChords(chords: List<ChordEntity>) {
        this.chords.value = this.chords.value.filter { chord ->
            !chords.any { it.id == chord.id }
        }
    }

    override suspend fun deleteByIds(chordIds: List<String>) {
        chords.value = chords.value.filter { it.id !in chordIds }
    }

    // Helper method for tests
    fun setChords(newChords: List<ChordEntity>) {
        chords.value = newChords
    }
}