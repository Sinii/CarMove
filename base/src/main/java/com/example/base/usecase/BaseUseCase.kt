package com.example.base.usecase

abstract class BaseUseCase<Params, Result> {

    abstract suspend fun doWork(params: Params): Result

}