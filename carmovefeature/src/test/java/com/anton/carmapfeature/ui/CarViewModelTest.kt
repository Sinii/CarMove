package com.anton.carmapfeature.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.usecase.car.CarCoordinatesAndAngleUpdaterUseCase
import com.example.utils.toTriple
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CarViewModelTest {
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
    fun `view model default one movement`() {
        val useCase = mockkClass(CarCoordinatesAndAngleUpdaterUseCase::class)
        val xyAngleTriple = 0f to 1f toTriple 2f
        every {
            runBlocking {
                useCase.doWork(any())
            }
        } returns CarCoordinatesAndAngleUpdaterUseCase.Result(arrayListOf(xyAngleTriple))
        val vm =
            CarViewModel(useCase)
        vm.destinationCoordinates(3f, 4f, 5f ,6f, 7f)
        Assert.assertTrue("xyAngleTriple.third = ${xyAngleTriple.third}", vm.carCoordinatesXYAngle.value == arrayListOf(xyAngleTriple))
    }

    @Test
    fun `view model default several movements`() {
        val useCase = mockkClass(CarCoordinatesAndAngleUpdaterUseCase::class)
        val xyAngleTriple = 0f to 1f toTriple 2f
        val movements = arrayListOf(xyAngleTriple, xyAngleTriple)
        every {
            runBlocking {
                useCase.doWork(any())
            }
        } returns CarCoordinatesAndAngleUpdaterUseCase.Result(movements)
        val vm =
            CarViewModel(useCase)
        vm.destinationCoordinates(3f, 4f, 5f ,6f, 7f)
        Assert.assertTrue("xyAngleTriple.third = ${xyAngleTriple.third}", vm.carCoordinatesXYAngle.value == movements)
    }
}