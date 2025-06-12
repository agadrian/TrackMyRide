package com.es.trackmyrideapp.di

import com.es.trackmyrideapp.data.remote.api.AuthApi
import com.es.trackmyrideapp.data.repository.AuthRepositoryImpl
import com.es.trackmyrideapp.data.repository.TokenRepositoryImpl
import com.es.trackmyrideapp.domain.repository.AuthRepository
import com.es.trackmyrideapp.domain.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Proporcionar independencia relacionadas con la autenticación
 */
@Module
@InstallIn(SingletonComponent::class)
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

    @Provides
    @Singleton
    fun provideTokenRepository(
        tokenRepositoryimpl: TokenRepositoryImpl
    ): TokenRepository = tokenRepositoryimpl

}