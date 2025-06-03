package com.es.trackmyrideapp.di

import com.es.trackmyrideapp.data.remote.api.RoutePinApi
import com.es.trackmyrideapp.data.repository.RoutePinRepositoryImpl
import com.es.trackmyrideapp.domain.repository.RoutePinRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoutePinsModule {

    @Provides
    @Singleton
    fun provideRoutePinRepository(
        routePinApi: RoutePinApi
    ): RoutePinRepository {
        return RoutePinRepositoryImpl(routePinApi)
    }

    @Provides
    @Singleton
    fun provideRoutePinApi(retrofit: Retrofit): RoutePinApi {
        return retrofit.create(RoutePinApi::class.java)
    }
}