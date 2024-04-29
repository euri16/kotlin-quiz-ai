package dev.euryperez.kotlinquizai.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import dev.euryperez.kotlinquizai.utils.DispatcherProvider
import dev.euryperez.kotlinquizai.utils.DispatcherProviderImpl

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = DispatcherProviderImpl()
}