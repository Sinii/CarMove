package com.anton.carmapfeature.ui

import androidx.lifecycle.MutableLiveData
import com.example.base.viewmodel.BaseViewModel
import com.example.utils.dLog
import com.example.utils.toTriple
import kotlinx.coroutines.Job
import java.lang.Math.toDegrees
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

class CarViewModel
@Inject constructor() : BaseViewModel() {
    val carCoordinatesXYAngle = MutableLiveData<Collection<Triple<Float, Float, Float>>>()
    val carXYAngle = MutableLiveData(Triple(1000f, 1000f, 0f))
    val carFinishXY = MutableLiveData(Pair(0f, 0f))
    val carAngle = MutableLiveData(0f)


    private var carCoordinateUpdater: Job? = null
    private var carAngleUpdater: Job? = null

    override fun doAutoMainWork() {
        "doAutoMainWork".dLog()
    }

    private fun carCoordinateUpdater(
        oldAngle: Float?,
        oldX: Float?,
        oldY: Float?
    ) = doWork {
        " carCoordinateUpdater ".dLog()
        var currentAngle = oldAngle ?: 0f
        var currentX = oldX ?: 0f
        var currentY = oldY ?: 0f
        val currentFinishXY = carFinishXY.value
        if (currentFinishXY != null) {
            val finishX = currentFinishXY.first
            val finishY = currentFinishXY.second

            // carXYAngle.postValue(Pair(currentFinishXY.first, currentFinishXY.second))
            "carCoordinateUpdater oldX = $oldX oldY = $oldY".dLog()

            val coordinates = ArrayList<Triple<Float, Float, Float>>()
            var carSpeed = 0f
            var carSpeedX = 0f
            var carSpeedY = 0f
            var carRotationSpeed = 0f

//            val angle = atan(
//                (abs(currentY) - abs(currentFinishXY.second)).toDouble()
//                        / (abs(currentX) - abs(currentFinishXY.first)).toDouble()
//            ).toFloat()
            // todo move to constants
            val carAcceleration = 2f //2 px per second per second
            val carRotationAcceleration = 1f //1 degree per second per second

            // todo move to constants
            val carMaxSpeed = 600f
            val carRotationMaxSpeed = 60f //2 px per second per second
            val finishAngle = atan(abs(finishY - currentY) / abs(finishX - currentX))

            val middleXPoint = abs(abs(currentX) - abs(finishX)) / 2 + (oldX ?: 0f)
            val middleAngle = abs(abs(currentAngle) - abs(finishAngle)) / 2

            while ((abs(abs(currentX) - abs(finishX)) > 10f)
                || (abs(abs(currentY) - abs(finishY)) > 10f)
            ) {
                val instantAcceleration =
                    if (currentX > middleXPoint) -carAcceleration else carAcceleration
                val instantRotationAcceleration =
                    if (currentAngle > middleAngle) -carRotationAcceleration else carRotationAcceleration

                "middleXPoint $middleXPoint instantAcceleration = $instantAcceleration ".dLog()
                if (abs(carSpeed + instantAcceleration) <= carMaxSpeed) {
                    carSpeed += instantAcceleration
                }
                if (abs(carRotationSpeed + instantRotationAcceleration) <= carRotationMaxSpeed) {
                    carRotationSpeed += instantRotationAcceleration
                }
                //val angle = atan(abs(finishY - currentY) / abs(finishX - currentX))

                "1 = ${(abs(abs(currentX) - abs(currentFinishXY.first)))} 2 = ${(abs(
                    abs(currentY) - abs(
                        currentFinishXY.second
                    )
                ))} ".dLog()

                currentAngle += carRotationMaxSpeed

                "currentX = $currentX finishX = ${currentFinishXY.first} currentY = $currentY finishY = ${currentFinishXY.second}".dLog()
                "carSpeed = $carSpeed carSpeedX = $carSpeedX carSpeedY = $carSpeedY ".dLog()
                carSpeedX = abs(carSpeed * cos(currentAngle))
                carSpeedY = abs(carSpeed * sin(currentAngle))
                currentX += (if (currentX > finishX) -carSpeedX else carSpeedX) / 30
                currentY += (if (currentY > finishY) -carSpeedY else carSpeedY) / 30

                val angleDegreeFloat = toDegrees(currentAngle.toDouble()).toFloat()
                coordinates.add(currentX to currentY toTriple angleDegreeFloat)
            }
            carCoordinatesXYAngle.postValue(coordinates)
        }
    }

    private fun carAngleUpdater() = doWork {
        "carAngleUpdater".dLog()
        val currentXY = carXYAngle.value
        val currentFinishXY = carFinishXY.value
        if (currentXY != null && currentFinishXY != null) {
            val finishAngle = Math
                .toDegrees(
                    atan(
                        (currentXY.second - currentFinishXY.second) / (currentXY.first - currentFinishXY.first)
                    ).toDouble()
                )
                .toFloat()
            carAngle.postValue(finishAngle)
//
//            var currentAngle = carAngle.value ?: 0f
//            val incrementAngle = if ((finishAngle - currentAngle) > 0) 1f else -1f
//            while (finishAngle.roundToInt() - currentAngle.roundToInt() != 0) {
//                "carAngleUpdater currentAngle = $currentAngle finishAngle = $finishAngle".dLog()
//                currentAngle += incrementAngle
//                carAngle.postValue(currentAngle)
//                delay(40L)
//            }

        }
    }

    fun destinationCoordinates(
        x: Float,
        y: Float,
        currentAngle: Float?,
        currentX: Float?,
        currentY: Float?
    ) {
        "destinationCoordinates".dLog()
        carFinishXY.postValue(Pair(x, y))
        carAngleUpdater?.cancel()
        carAngleUpdater = carAngleUpdater()
        carCoordinateUpdater?.cancel()
        carCoordinateUpdater = carCoordinateUpdater(currentAngle, currentX, currentY)
    }

    override fun onCleared() {
        carCoordinateUpdater?.cancel()
        carAngleUpdater?.cancel()
        super.onCleared()
    }
}