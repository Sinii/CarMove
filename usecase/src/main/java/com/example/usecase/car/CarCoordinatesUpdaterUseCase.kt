package com.example.usecase.car

import com.example.base.usecase.BaseUseCase
import com.example.utils.toTriple
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

class CarCoordinatesUpdaterUseCase @Inject constructor() :
    BaseUseCase<CarCoordinatesUpdaterUseCase.Params, CarCoordinatesUpdaterUseCase.Result>() {
    override suspend fun doWork(params: Params): Result {

        val coordinates = ArrayList<Triple<Float, Float, Float>>()
        var currentAngle = params.oldAngle
        var currentX = params.oldX
        var currentY = params.oldY
        val finishX = params.finishX
        val finishY = params.finishY

        var carSpeed = 0f
        var carSpeedX = 0f
        var carSpeedY = 0f
        var carRotationSpeed = 0f

        // todo move to constants
        val carAcceleration = 2f //2 px per second per second
        val carRotationAcceleration = .05f //.1 degree per second per second

        val carRotationMaxSpeed = 1f //2 px per second per second

        val middleXPoint = abs(abs(currentX) - abs(finishX)) / 2 + (params.oldX ?: 0f)
        val finishAngle = atan(abs(finishY - currentY) / abs(finishX - currentX))

        while ((abs(abs(currentX) - abs(finishX)) > 30f)
            || (abs(abs(currentY) - abs(finishY)) > 30f)
        ) {
            val instantFinishAngle = atan(abs(finishY - currentY) / abs(finishX - currentX))
            val middleAngle = abs(abs(currentAngle) - abs(instantFinishAngle)) / 2
            val instantAcceleration =
                if (currentX > middleXPoint) -carAcceleration else carAcceleration
            val instantRotationAcceleration =
                if (currentAngle > middleAngle) -carRotationAcceleration else carRotationAcceleration

            if (abs(carSpeed + instantAcceleration) <= CAR_MAX_SPEED) {
                carSpeed += instantAcceleration
            }
            if (abs(carRotationSpeed + instantRotationAcceleration) <= carRotationMaxSpeed) {
                carRotationSpeed += instantRotationAcceleration
            }
            //val angle = atan(abs(finishY - currentY) / abs(finishX - currentX))

            if (abs(currentAngle - instantFinishAngle) > .4f) {
                currentAngle += carRotationSpeed
            } else {
                carRotationSpeed = 0f
            }
            // todo fix <stair> movement with dynamic angle hist will be B solution
            carSpeedX = abs(carSpeed * cos(finishAngle))
            carSpeedY = abs(carSpeed * sin(finishAngle))
            val dx = (if (currentX > finishX) -carSpeedX else carSpeedX) / 10
            val dy = (if (currentY > finishY) -carSpeedY else carSpeedY) / 10
            currentX += dx
            currentY += dy
            val angleDegreeFloat = Math.toDegrees(currentAngle.toDouble()).toFloat()
            coordinates.add(dx to dy toTriple angleDegreeFloat)
        }
        return Result(coordinates)
    }


    class Params(val oldX: Float, val oldY: Float, val oldAngle: Float, val finishX: Float, val finishY: Float)
    class Result(val coordinates: ArrayList<Triple<Float, Float, Float>>)

    companion object {
        const val CAR_MAX_SPEED = 200f

    }
}