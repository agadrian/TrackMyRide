package com.es.trackmyrideapp.di

import com.es.trackmyrideapp.data.remote.api.RouteApi
import com.es.trackmyrideapp.data.repository.RouteRepositoryImpl
import com.es.trackmyrideapp.domain.repository.RouteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Proporcionar independencia relacionada con las rutas
 */
@Module
@InstallIn(SingletonComponent::class)
object RouteModule {

    @Provides
    @Singleton
    fun provideRouteRepository(
        routeApi: RouteApi
    ): RouteRepository {
        return RouteRepositoryImpl(routeApi)
    }

    @Provides
    @Singleton
    fun provideRouteApi(retrofit: Retrofit): RouteApi {
        return retrofit.create(RouteApi::class.java)
    }
}