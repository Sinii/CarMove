package com.example.usecase.exchangerates

import com.example.base.usecase.BaseUseCase
import com.example.jobs.Job
import com.example.repository.jobs.ExampleRepository
import javax.inject.Inject

class ExampleUseCase
@Inject constructor(
    private val exampleRepository: ExampleRepository
) : BaseUseCase<ExampleUseCase.Params, ExampleUseCase.Result>() {
    override suspend fun doWork(params: Params): Result {
        val result = exampleRepository
            .doWork(ExampleRepository.Params(params.description, params.location))
        val rates = result.jobs
        val errorMessage = result.error?.error
        return Result(rates, errorMessage)
    }

    class Params(val description: String, val location: String?)
    class Result(val jobs: List<Job>?, val errorMessage: String?)
}