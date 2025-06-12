package com.es.trackmyrideapp.di


import com.es.trackmyrideapp.data.remote.api.VehicleApi
import com.es.trackmyrideapp.data.repository.VehicleRepositoryImpl
import com.es.trackmyrideapp.domain.repository.VehicleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Proporcionar independencia relacionada con los vehículos
 */
@Module
@InstallIn(SingletonComponent::class)
object VehicleModule {

    @Provides
    @Singleton
    fun provideVehicleRepository(
        vehicleApi: VehicleApi
    ): VehicleRepository {
        return VehicleRepositoryImpl(vehicleApi)
    }

    @Provides
    @Singleton
    fun provideVehicleApi(retrofit: Retrofit): VehicleApi {
        return retrofit.create(VehicleApi::class.java)
    }
}