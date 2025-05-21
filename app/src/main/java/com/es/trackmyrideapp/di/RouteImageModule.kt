package com.es.trackmyrideapp.di

import com.es.trackmyrideapp.data.remote.api.RouteImageApi
import com.es.trackmyrideapp.data.repository.RouteImageRepositoryImpl
import com.es.trackmyrideapp.domain.repository.RouteImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RouteImageModule {

    @Provides
    @Singleton
    fun provideRouteImageApi(retrofit: Retrofit): RouteImageApi {
        return retrofit.create(RouteImageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRouteImageRepository(
        routeImageApi: RouteImageApi
    ): RouteImageRepository {
        return RouteImageRepositoryImpl(routeImageApi)
    }
}