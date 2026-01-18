package cz.mendelu.pef.chordsnap.fake

import cz.mendelu.pef.chordsnap.communication.IChordsRemoteRepository
import cz.mendelu.pef.chordsnap.di.RemoteRepositoryModule
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RemoteRepositoryModule::class]
)
abstract class FakeRemoteRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChordsRemoteRepository(
        fakeRepository: FakeChordsRemoteRepositoryImpl
    ): IChordsRemoteRepository
}