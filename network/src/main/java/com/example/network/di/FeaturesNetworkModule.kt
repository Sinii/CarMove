package com.example.network.di

import com.example.network.retrofit.RetrofitJobList
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class FeaturesNetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitJobList(retrofit: Retrofit): RetrofitJobList = retrofit
        .create(RetrofitJobList::class.java)

}
