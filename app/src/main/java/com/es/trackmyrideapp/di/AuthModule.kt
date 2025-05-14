package com.es.trackmyrideapp.di

import com.es.trackmyrideapp.data.remote.api.AuthApi
import com.es.trackmyrideapp.data.repository.AuthRepositoryImpl
import com.es.trackmyrideapp.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Asegura que se cree una única instancia en el ciclo de vida de la aplicación
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository {
        return authRepositoryImpl
    }

    // Inyectar AuthAPI
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
}