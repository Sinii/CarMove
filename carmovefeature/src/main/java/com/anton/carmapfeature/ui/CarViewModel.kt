package com.anton.carmapfeature.ui

import androidx.lifecycle.MutableLiveData
import com.example.base.viewmodel.BaseViewModel
import com.example.usecase.car.CarCoordinatesAndAngleUpdaterUseCase
import com.example.utils.dLog
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlin.math.atan

class CarViewModel
@Inject constructor(
    private val carCoordinatesAndAngleUpdaterUseCase: CarCoordinatesAndAngleUpdaterUseCase
) : BaseViewModel() {
    val carCoordinatesXYAngle = MutableLiveData<Collection<Triple<Float, Float, Float>>>()
    val carXYAngle = MutableLiveData(Triple(0f, 0f, 0f))
    val carFinishXY = MutableLiveData(Pair(0f, 0f))
    val carAngle = MutableLiveData(0f)

    private var carCoordinateUpdater: Job? = null

    override fun doAutoMainWork() {
        "doAutoMainWork".dLog()
    }

    private fun carUpdater(
        oldAngle: Float,
        oldX: Float,
        oldY: Float
    ) = doWork {
        " carUpdater ".dLog()
        val currentFinishXY = carFinishXY.value
        if (currentFinishXY != null) {
            val finishX = currentFinishXY.first
            val finishY = currentFinishXY.second
            val finishAngle =
                atan(
                    (oldY - currentFinishXY.second) / (oldX - currentFinishXY.first)
                )
            carAngle.postValue(finishAngle)

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
        carCoordinateUpdater?.cancel()
        carCoordinateUpdater = carUpdater(currentAngle, currentX, currentY)
    }

    override fun onCleared() {
        carCoordinateUpdater?.cancel()
        super.onCleared()
    }
}