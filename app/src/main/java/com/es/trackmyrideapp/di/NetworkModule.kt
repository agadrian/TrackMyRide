package com.es.trackmyrideapp.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.remote.AuthInterceptor
import com.es.trackmyrideapp.data.remote.TokenAuthenticator
import com.es.trackmyrideapp.domain.repository.TokenRepository
import com.es.trackmyrideapp.utils.LocalDateTimeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    //private const val BASE_URL = "http://192.168.1.134:8080"
    private const val BASE_URL = "https://613f-79-116-214-36.ngrok-free.app"

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    @Provides
    @Singleton
    fun provideAuthInterceptor(authPreferences: AuthPreferences): AuthInterceptor {
        return AuthInterceptor(authPreferences)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenRepository: dagger.Lazy<TokenRepository>
    ): TokenAuthenticator {
        return TokenAuthenticator(tokenRepository)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            // Timeouts para las peticiones a la API
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(authInterceptor) // Añade token si existe
            .authenticator(tokenAuthenticator) // Refresca si hay 401
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }
}