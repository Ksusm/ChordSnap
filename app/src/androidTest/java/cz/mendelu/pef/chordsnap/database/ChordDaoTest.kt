package cz.mendelu.pef.chordsnap.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ChordDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var chordDao: ChordDao

    private val testChords = listOf(
        ChordEntity("1", "C major", "Do mayor", "C", "major", "url1"),
        ChordEntity("2", "G minor", "Sol menor", "G", "minor", "url2"),
        ChordEntity("3", "D major", "Re mayor", "D", "major", "url3")
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        chordDao = database.chordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAll_and_getAllChords_returnsAllChords() = runTest {
        chordDao.insertAll(testChords)

        val chords = chordDao.getAllChords().first()
        assertEquals(3, chords.size)
    }

    @Test
    fun insertAll_with_conflict_replaces_chord() = runTest {
        chordDao.insertAll(testChords)

        val updatedChord = ChordEntity("1", "C minor", "Do menor", "C", "minor", "url_new")
        chordDao.insertAll(listOf(updatedChord))

        val chords = chordDao.getAllChords().first()
        assertEquals(3, chords.size)
        assertEquals("C minor", chords.find { it.id == "1" }?.nameEng)
    }

    @Test
    fun count_returns_correct_number() = runTest {
        chordDao.insertAll(testChords)

        val count = chordDao.count()
        assertEquals(3, count)
    }

    @Test
    fun deleteByIds_removes_specific_chords() = runTest {
        chordDao.insertAll(testChords)

        chordDao.deleteByIds(listOf("1", "2"))

        val chords = chordDao.getAllChords().first()
        assertEquals(1, chords.size)
        assertEquals("3", chords[0].id)
    }

    @Test
    fun deleteChords_removes_chords() = runTest {
        chordDao.insertAll(testChords)

        chordDao.deleteChords(listOf(testChords[0]))

        val chords = chordDao.getAllChords().first()
        assertEquals(2, chords.size)
    }

    @Test
    fun getAllChords_returns_empty_list_when_no_chords() = runTest {
        val chords = chordDao.getAllChords().first()
        assertTrue(chords.isEmpty())
    }
}