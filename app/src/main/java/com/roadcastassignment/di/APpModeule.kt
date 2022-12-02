package com.roadcastassignment.di

import android.app.Application
import com.roadcastassignment.network.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object APpModeule {

    @Provides
    @Singleton
    fun provideMoshi() : Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideApiServer(moshi : Moshi) : ApiService = Retrofit.Builder().run {
        baseUrl(ApiService.BASE_ULR)
        addConverterFactory(MoshiConverterFactory.create(moshi))
        build()
    }.create(ApiService::class.java)


}