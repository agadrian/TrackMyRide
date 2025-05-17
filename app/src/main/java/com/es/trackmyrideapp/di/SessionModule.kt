package com.es.trackmyrideapp.di

import com.es.trackmyrideapp.data.repository.SessionRepository
import com.es.trackmyrideapp.data.repository.SessionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {

    @Provides
    @Singleton
    fun provideSessionRepository(): SessionRepository {
        return SessionRepositoryImpl()
    }
}