package cz.mendelu.pef.chordsnap.di

import cz.mendelu.pef.chordsnap.communication.ChordsAPI
import cz.mendelu.pef.chordsnap.communication.ChordsRemoteRepositoryImpl
import cz.mendelu.pef.chordsnap.communication.IChordsRemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteRepositoryModule {

    @Provides
    @Singleton
    fun provideChordsRemoteRepository(
        chordsAPI: ChordsAPI
    ): IChordsRemoteRepository {
        return ChordsRemoteRepositoryImpl(chordsAPI)
    }
}