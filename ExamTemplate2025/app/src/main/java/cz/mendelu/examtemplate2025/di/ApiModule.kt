package cz.mendelu.examtemplate2025.di

import cz.mendelu.examtemplate2025.api.ParksApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideParksApi(retrofit: Retrofit): ParksApi {
        return retrofit.create(ParksApi::class.java)
    }
}
