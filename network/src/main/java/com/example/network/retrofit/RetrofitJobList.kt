package com.example.network.retrofit

import com.example.jobs.Job
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitJobList {

    @GET("positions.json")
    suspend fun getJobListAsync(
        @Query("description") description: String,
        @Query("location") location: String?
    ): Response<List<Job>>
}
