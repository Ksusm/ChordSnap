package cz.mendelu.examtemplate2025.di

import cz.mendelu.examtemplate2025.repository.ParksRepository
import cz.mendelu.examtemplate2025.repository.ParksRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindParksRepository(impl: ParksRepositoryImpl): ParksRepository

}