package cz.mendelu.pef.chordsnap.fake

import cz.mendelu.pef.chordsnap.database.PracticeDao
import cz.mendelu.pef.chordsnap.database.PracticeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePracticeDao : PracticeDao {

    private val practices = MutableStateFlow<List<PracticeEntity>>(emptyList())

    override fun getAllPractices(): Flow<List<PracticeEntity>> {
        return practices
    }

    override suspend fun getPracticeById(practiceId: Long): PracticeEntity? {
        return practices.value.find { it.id == practiceId }
    }

    override suspend fun insertPractice(practice: PracticeEntity): Long {
        val newId = (practices.value.maxOfOrNull { it.id } ?: 0) + 1
        val newPractice = practice.copy(id = newId)
        practices.value = practices.value + newPractice
        return newId
    }

    override suspend fun updatePractice(practice: PracticeEntity) {
        practices.value = practices.value.map {
            if (it.id == practice.id) practice else it
        }
    }

    override suspend fun deletePractice(practice: PracticeEntity) {
        practices.value = practices.value.filter { it.id != practice.id }
    }

    override suspend fun deletePracticeById(practiceId: Long) {
        practices.value = practices.value.filter { it.id != practiceId }
    }

    // Helper method for tests to set practices
    fun setPractices(newPractices: List<PracticeEntity>) {
        practices.value = newPractices
    }
}