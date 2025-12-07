package cz.mendelu.pef.chordsnap.di

import cz.mendelu.pef.chordsnap.communication.ChordsAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object APIModule {

    @Provides
    @Singleton
    fun provideChordsAPI(retrofit: Retrofit): ChordsAPI {
        return retrofit.create(ChordsAPI::class.java)
    }
}