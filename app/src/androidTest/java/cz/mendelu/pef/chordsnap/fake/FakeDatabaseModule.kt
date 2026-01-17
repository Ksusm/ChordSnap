package cz.mendelu.pef.chordsnap.fake

import cz.mendelu.pef.chordsnap.database.ChordDao
import cz.mendelu.pef.chordsnap.database.PracticeDao
import cz.mendelu.pef.chordsnap.di.DatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object FakeDatabaseModule {

    @Provides
    @Singleton
    fun provideFakePracticeDao(): PracticeDao {
        return FakePracticeDao()
    }

    @Provides
    @Singleton
    fun provideFakeChordDao(): ChordDao {
        return FakeChordDao()
    }
}