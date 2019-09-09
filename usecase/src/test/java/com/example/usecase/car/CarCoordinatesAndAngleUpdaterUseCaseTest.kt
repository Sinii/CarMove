package com.example.usecase.car

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.usecase.car.CarCoordinatesAndAngleUpdaterUseCase.Companion.COORDINATION_CONDITION
import com.example.utils.toTriple
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CarCoordinatesAndAngleUpdaterUseCaseTest {
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
    fun `no movement`() {
        val resultCoordinates = arrayListOf<Triple<Float, Float, Float>>()
        val useCase = CarCoordinatesAndAngleUpdaterUseCase()
        runBlocking {
            val result = useCase.doWork(
                CarCoordinatesAndAngleUpdaterUseCase.Params(
                    0f,
                    0f,
                    0f,
                    1f,
                    1f
                )
            )
            result.coordinates.forEach {
                println("tripple = ${it.first} ${it.second} ${it.third}")
            }
            Assert.assertTrue(
                result.coordinates == resultCoordinates
            )

        }
    }


    @Test
    fun `simple movement`() {
        val resultCoordinates = arrayListOf(
            0.0f to 0.0f toTriple 0.02f,
            0.0f to 0.0f toTriple 0.04f,
            0.0f to 0.0f toTriple 0.06f,
            0.0f to 0.0f toTriple 0.08f,
            0.0f to 0.0f toTriple 0.099999994f,
            0.0f to 0.0f toTriple 0.11999999f,
            0.0f to 0.0f toTriple 0.13999999f,
            0.0f to 0.0f toTriple 0.15999998f,
            0.0f to 0.0f toTriple 0.17999998f,
            0.0f to 0.0f toTriple 0.19999997f,
            0.0f to 0.0f toTriple 0.21999997f,
            0.0f to 0.0f toTriple 0.23999996f,
            0.0f to 0.0f toTriple 0.25999996f,
            0.0f to 0.0f toTriple 0.27999997f,
            0.0f to 0.0f toTriple 0.29999998f,
            0.0f to 0.0f toTriple 0.32f,
            0.0f to 0.0f toTriple 0.34f,
            0.0f to 0.0f toTriple 0.36f,
            0.0f to 0.0f toTriple 0.38000003f,
            0.0f to 0.0f toTriple 0.40000004f,
            0.0f to 0.0f toTriple 0.42000005f,
            0.0f to 0.0f toTriple 0.44000006f,
            0.0f to 0.0f toTriple 0.46000007f,
            0.0f to 0.0f toTriple 0.48000008f,
            0.0f to 0.0f toTriple 0.50000006f,
            0.0f to 0.0f toTriple 0.52000004f,
            0.0f to 0.0f toTriple 0.54f,
            0.0f to 0.0f toTriple 0.56f,
            0.0f to 0.0f toTriple 0.58f,
            0.0f to 0.0f toTriple 0.59999996f,
            0.0f to 0.0f toTriple 0.61999995f,
            0.0f to 0.0f toTriple 0.6399999f,
            0.0f to 0.0f toTriple 0.6599999f,
            0.0f to 0.0f toTriple 0.6799999f,
            0.0f to 0.0f toTriple 0.69999987f,
            0.0f to 0.0f toTriple 0.71999985f,
            0.0f to 0.0f toTriple 0.73999983f,
            0.14142135f to 0.14142135f toTriple 0.73999983f,
            0.2828427f to 0.2828427f toTriple 0.73999983f,
            0.42426404f to 0.42426404f toTriple 0.73999983f,
            0.5656854f to 0.5656854f toTriple 0.73999983f
        )
        val useCase = CarCoordinatesAndAngleUpdaterUseCase()
        runBlocking {
            val result = useCase.doWork(
                CarCoordinatesAndAngleUpdaterUseCase.Params(
                    0f,
                    0f,
                    0f,
                    COORDINATION_CONDITION + 1f,
                    COORDINATION_CONDITION + 1f
                )
            )
            result.coordinates.forEach {
                println("${it.first}f to ${it.second}f toTriple ${it.third}f,")
            }
            Assert.assertTrue(
                result.coordinates == resultCoordinates
            )
        }
    }
}