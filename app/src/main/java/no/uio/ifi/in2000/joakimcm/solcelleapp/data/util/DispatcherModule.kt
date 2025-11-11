package no.uio.ifi.in2000.joakimcm.solcelleapp.data.util

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Tells Hilt how to make the dispatchers
@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
}