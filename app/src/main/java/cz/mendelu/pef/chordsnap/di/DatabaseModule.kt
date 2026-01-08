package cz.mendelu.pef.chordsnap.di

import android.content.Context
import androidx.room.Room
import cz.mendelu.pef.chordsnap.database.AppDatabase
import cz.mendelu.pef.chordsnap.database.ChordDao
import cz.mendelu.pef.chordsnap.database.PracticeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "chordsnap_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideChordDao(database: AppDatabase): ChordDao {
        return database.chordDao()
    }

    @Provides
    @Singleton
    fun providePracticeDao(database: AppDatabase): PracticeDao {
        return database.practiceDao()
    }
}
