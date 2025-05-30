package com.es.trackmyrideapp.di

import com.es.trackmyrideapp.data.remote.api.ProfileImageApi
import com.es.trackmyrideapp.data.repository.ProfileImageRepositoryImpl
import com.es.trackmyrideapp.domain.repository.ProfileImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileImageModule {

    @Provides
    @Singleton
    fun provideProfileImageApi(retrofit: Retrofit): ProfileImageApi {
        return retrofit.create(ProfileImageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileImageRepository(
        profileImageApi: ProfileImageApi
    ): ProfileImageRepository {
        return ProfileImageRepositoryImpl(profileImageApi)
    }
}