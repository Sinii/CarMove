package com.anton.carmapfeature.ui

import androidx.lifecycle.MutableLiveData
import com.example.base.viewmodel.BaseViewModel
import com.example.usecase.car.CarCoordinatesUpdaterUseCase
import com.example.utils.dLog
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlin.math.atan

class CarViewModel
@Inject constructor(
    private val carCoordinatesUpdaterUseCase: CarCoordinatesUpdaterUseCase
) : BaseViewModel() {
    val carCoordinatesXYAngle = MutableLiveData<Collection<Triple<Float, Float, Float>>>()
    val carXYAngle = MutableLiveData(Triple(0f, 0f, 0f))
    val carFinishXY = MutableLiveData(Pair(0f, 0f))
    val carAngle = MutableLiveData(0f)


    private var carCoordinateUpdater: Job? = null
    private var carAngleUpdater: Job? = null

    override fun doAutoMainWork() {
        "doAutoMainWork".dLog()
    }

    private fun carCoordinateUpdater(
        oldAngle: Float,
        oldX: Float,
        oldY: Float
    ) = doWork {
        " carCoordinateUpdater ".dLog()
        val currentFinishXY = carFinishXY.value
        if (currentFinishXY != null) {
            val finishX = currentFinishXY.first
            val finishY = currentFinishXY.second
            val coordinates = carCoordinatesUpdaterUseCase
                .doWork(
                    CarCoordinatesUpdaterUseCase.Params(oldX, oldY, oldAngle, finishX, finishY)
                )
                .coordinates
            carCoordinatesXYAngle.postValue(coordinates)
        }
    }

    private fun carAngleUpdater(
        oldX: Float,
        oldY: Float
    ) = doWork {
        "carAngleUpdater carFinishXY = ${carFinishXY.value?.first} carFinishXY = ${carFinishXY.value?.second}".dLog()

        val currentFinishXY = carFinishXY.value
        if (currentFinishXY != null) {
            val finishAngle =
                atan(
                    (oldY - currentFinishXY.second) / (oldX - currentFinishXY.first)
                )
            carAngle.postValue(finishAngle)
        }
    }

    fun destinationCoordinates(
        x: Float,
        y: Float,
        currentAngle: Float,
        currentX: Float,
        currentY: Float
    ) {
        "destinationCoordinates".dLog()
        carFinishXY.postValue(Pair(x, y))
        carAngleUpdater?.cancel()
        carAngleUpdater = carAngleUpdater(currentX, currentY)
        carCoordinateUpdater?.cancel()
        carCoordinateUpdater = carCoordinateUpdater(currentAngle, currentX, currentY)
    }

    override fun onCleared() {
        carCoordinateUpdater?.cancel()
        carAngleUpdater?.cancel()
        super.onCleared()
    }
}