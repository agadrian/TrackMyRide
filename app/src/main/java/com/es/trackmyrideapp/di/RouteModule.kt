package com.es.trackmyrideapp.di

import com.es.trackmyrideapp.data.remote.api.RouteApi
import com.es.trackmyrideapp.data.repository.RouteRepositoryImpl
import com.es.trackmyrideapp.domain.repository.RouteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
}