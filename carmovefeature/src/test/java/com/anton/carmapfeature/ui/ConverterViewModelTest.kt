package com.anton.carmapfeature.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConverterViewModelTest {
    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        mockkStatic(Dispatchers::class)
        every {
            Dispatchers.IO
        } returns Dispatchers.Main
    }

    @Test
    fun `test no items state default `() {
//        val getCalculatedRateItemsUseCase = mockkClass(GetJobItemsUseCase::class)
//        every {
//            runBlocking {
//                getCalculatedRateItemsUseCase.doWork(any())
//            }
//        } returns GetJobItemsUseCase.Result(emptyList(), null)
//        val vm =
//            CarViewModel(getCalculatedRateItemsUseCase)
//        assert(vm.showNoItemsStub.value == View.GONE)
    }
}