package cz.mendelu.pef.chordsnap.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PracticeDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var practiceDao: PracticeDao

    private val testPractices = listOf(
        PracticeEntity(0, "Strumming basics", "1,2,3"),
        PracticeEntity(0, "Moon river", "4,5,6,7,8")
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        practiceDao = database.practiceDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertPractice_and_getAllPractices_returnsAll() = runTest {
        testPractices.forEach { practiceDao.insertPractice(it) }

        val practices = practiceDao.getAllPractices().first()
        assertEquals(2, practices.size)
    }

    @Test
    fun insertPractice_returns_generated_id() = runTest {
        val id = practiceDao.insertPractice(testPractices[0])

        assert(id > 0)
    }

    @Test
    fun getPracticeById_returns_correct_practice() = runTest {
        val id = practiceDao.insertPractice(testPractices[0])

        val practice = practiceDao.getPracticeById(id)

        assertNotNull(practice)
        assertEquals("Strumming basics", practice?.name)
    }

    @Test
    fun getPracticeById_returns_null_when_not_found() = runTest {
        val practice = practiceDao.getPracticeById(999L)

        assertNull(practice)
    }

    @Test
    fun updatePractice_updates_existing_practice() = runTest {
        val id = practiceDao.insertPractice(testPractices[0])
        val practice = practiceDao.getPracticeById(id)!!

        val updated = practice.copy(name = "Updated Name")
        practiceDao.updatePractice(updated)

        val result = practiceDao.getPracticeById(id)
        assertEquals("Updated Name", result?.name)
    }

    @Test
    fun deletePracticeById_removes_practice() = runTest {
        val id = practiceDao.insertPractice(testPractices[0])

        practiceDao.deletePracticeById(id)

        val practice = practiceDao.getPracticeById(id)
        assertNull(practice)
    }

    @Test
    fun deletePractice_removes_practice() = runTest {
        val id = practiceDao.insertPractice(testPractices[0])
        val practice = practiceDao.getPracticeById(id)!!

        practiceDao.deletePractice(practice)

        val result = practiceDao.getPracticeById(id)
        assertNull(result)
    }

    @Test
    fun getAllPractices_returns_in_descending_order() = runTest {
        val id1 = practiceDao.insertPractice(testPractices[0])
        val id2 = practiceDao.insertPractice(testPractices[1])

        val practices = practiceDao.getAllPractices().first()

        assertEquals(id2, practices[0].id)
        assertEquals(id1, practices[1].id)
    }
}