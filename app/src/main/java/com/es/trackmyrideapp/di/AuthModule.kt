package com.es.trackmyrideapp.di

import com.es.trackmyrideapp.data.repository.AuthRepositoryImpl
import com.es.trackmyrideapp.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Asegura que se cree una única instancia en el ciclo de vida de la aplicación
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository {
        return authRepositoryImpl
    }
}