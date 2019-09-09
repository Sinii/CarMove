package com.anton.carmapfeature.ui

import androidx.lifecycle.MutableLiveData
import com.example.base.viewmodel.BaseViewModel
import com.example.usecase.car.CarCoordinatesAndAngleUpdaterUseCase
import com.example.utils.dLog
import kotlinx.coroutines.Job
import javax.inject.Inject

class CarViewModel
@Inject constructor(
    private val carCoordinatesAndAngleUpdaterUseCase: CarCoordinatesAndAngleUpdaterUseCase
) : BaseViewModel() {
    val carCoordinatesXYAngle = MutableLiveData<Collection<Triple<Float, Float, Float>>>()

    private var carFinishXY = 0f to 0f
    private var carCoordinateUpdater: Job? = null

    override fun doAutoMainWork() {}

    private fun carUpdater(
        oldAngle: Float,
        oldX: Float,
        oldY: Float
    ) = doWork {
        " carUpdater ".dLog()
        val currentFinishXY = carFinishXY
        val finishX = currentFinishXY.first
        val finishY = currentFinishXY.second
        val coordinates = carCoordinatesAndAngleUpdaterUseCase
            .doWork(
                CarCoordinatesAndAngleUpdaterUseCase.Params(
                    oldX,
                    oldY,
                    oldAngle,
                    finishX,
                    finishY
                )
            )
            .coordinates
        carCoordinatesXYAngle.postValue(coordinates)
    }

    fun destinationCoordinates(
        x: Float,
        y: Float,
        currentAngle: Float,
        currentX: Float,
        currentY: Float
    ) {
        "destinationCoordinates".dLog()
        carFinishXY = x to y
        carCoordinateUpdater?.cancel()
        carCoordinateUpdater = carUpdater(currentAngle, currentX, currentY)
    }

    override fun onCleared() {
        carCoordinateUpdater?.cancel()
        super.onCleared()
    }
}