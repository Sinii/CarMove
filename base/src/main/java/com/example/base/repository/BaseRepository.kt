package com.example.base.repository

import com.example.ApiError
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject

abstract class BaseRepository<Params, Result> {

    @Inject
    lateinit var retrofit: Retrofit

    abstract suspend fun doWork(params: Params): Result

    fun getError(errorBody: ResponseBody?): ApiError? {
        var apiError: ApiError? = null
        if (errorBody != null) {
            val converter: Converter<ResponseBody, ApiError> =
                retrofit.responseBodyConverter(ApiError::class.java, arrayOfNulls<Annotation>(0))
            try {
                val error = converter.convert(errorBody)
                apiError = error
            } catch (e: IOException) {
                //e.printStackTrace()
            }
        }
        return apiError
    }
}