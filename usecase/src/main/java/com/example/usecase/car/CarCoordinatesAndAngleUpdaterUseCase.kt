package com.example.usecase.car

import com.example.base.usecase.BaseUseCase
import com.example.utils.dLog
import com.example.utils.toTriple
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

class CarCoordinatesAndAngleUpdaterUseCase @Inject constructor() :
    BaseUseCase<CarCoordinatesAndAngleUpdaterUseCase.Params, CarCoordinatesAndAngleUpdaterUseCase.Result>() {
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

        val middleXPoint = abs(abs(currentX) - abs(finishX)) / 2 + params.oldX
        val finishAngle = atan(abs(finishY - currentY) / abs(finishX - currentX))

        while ((abs(abs(currentX) - abs(finishX)) > COORDINATION_CONDITION)
            || (abs(abs(currentY) - abs(finishY)) > COORDINATION_CONDITION)
        ) {
            var dx = 0f
            var dy = 0f
            if (abs(abs(currentAngle) - abs(finishAngle)) > ANGLE_CONDITION) {
                " run currentAngle = $currentAngle finishAngle = $finishAngle".dLog()
                currentAngle += if (currentAngle > finishAngle) -DELTA_ANGLE else DELTA_ANGLE
            } else {
                val instantFinishAngle = atan(abs(finishY - currentY) / abs(finishX - currentX))
                val middleAngle = abs(abs(currentAngle) - abs(instantFinishAngle)) / 2
                val instantAcceleration =
                    if (currentX > middleXPoint) -CAR_ACCELERATION else CAR_ACCELERATION
                val instantRotationAcceleration =
                    if (currentAngle > middleAngle) -CAR_ROTATION_ACCELERATION else CAR_ROTATION_ACCELERATION

                if (abs(carSpeed + instantAcceleration) <= CAR_MAX_SPEED) {
                    carSpeed += instantAcceleration
                }
                if (abs(carRotationSpeed + instantRotationAcceleration) <= CAR_ROTATION_MAX_SPEED) {
                    carRotationSpeed += instantRotationAcceleration
                }
                //val angle = atan(abs(finishY - currentY) / abs(finishX - currentX))

                if (abs(currentAngle - instantFinishAngle) > ANGLE_CONDITION) {
                    currentAngle += carRotationSpeed
                } else {
                    carRotationSpeed = 0f
                }
                // todo fix <stair> movement with dynamic angle hist will be B solution
                carSpeedX = abs(carSpeed * cos(finishAngle))
                carSpeedY = abs(carSpeed * sin(finishAngle))
                dx = (if (currentX > finishX) -carSpeedX else carSpeedX) / 10
                dy = (if (currentY > finishY) -carSpeedY else carSpeedY) / 10
                currentX += dx
                currentY += dy
            }
            coordinates.add(dx to dy toTriple currentAngle)
        }
        return Result(coordinates)
    }


    class Params(
        val oldX: Float,
        val oldY: Float,
        val oldAngle: Float,
        val finishX: Float,
        val finishY: Float
    )

    class Result(val coordinates: ArrayList<Triple<Float, Float, Float>>)

    companion object {
        const val CAR_MAX_SPEED = 200f
        const val DELTA_ANGLE = .2f
        const val ANGLE_CONDITION = .4f
        const val COORDINATION_CONDITION = 30f


        const val CAR_ACCELERATION = 2f//2 px per second per second
        const val CAR_ROTATION_ACCELERATION = .05f //.1 degree per second per second
        const val CAR_ROTATION_MAX_SPEED = 1f //2 px per second per second
    }
}