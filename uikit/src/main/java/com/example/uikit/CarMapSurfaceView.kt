package com.example.uikit

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class CarMapSurfaceView : SurfaceView, SurfaceHolder.Callback {

    private var drawThread: DrawThread? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init {
        holder.addCallback(this)
    }

    fun updateAngle(angle: Float) {
        drawThread?.finishAngle = angle
    }

    fun getCarX() = drawThread?.currentX ?: 0f
    fun getCarY() = drawThread?.currentY ?: 0f
    fun getAngle() = drawThread?.currentAngle ?: 0f

    fun addCoordinatesTriple(xyPair: Triple<Float, Float, Float>) {
        drawThread?.addCoordinatesTriple(xyPair)
    }

    fun addAllCoordinates(coordinates: Collection<Triple<Float, Float, Float>>) {
        drawThread?.addAllCoordinates(coordinates)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawThread = DrawThread(getHolder(), resources)
            .apply {
                running = true
                start()
            }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        drawThread?.running = false
        while (retry) {
            try {
                drawThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    internal class DrawThread(
        private val surfaceHolder: SurfaceHolder, resources: Resources
    ) : Thread() {
        var finishAngle = 0f
        var currentAngle = 0f
        var running = true
        var finishX = 0f
        var finishY = 0f
        var currentX = 0f
        var currentY = 0f

        private val movementCoordinates = ConcurrentLinkedQueue<Triple<Float, Float, Float>>()
        private val carWidth = 250
        private val carHeight = 150
        private val carHalfWidth = carWidth.toFloat() / 2
        private val carHalfHeight = carHeight.toFloat() / 2

        private val halfDiagonal =
            sqrt((carWidth * carWidth + carHeight * carHeight).toDouble()) / 2
        private val diagonalAngle = atan(carHeight / carWidth.toDouble())
        private var prevTime = 0L
        private val paint = Paint()

        init {
            prevTime = System.currentTimeMillis()
            paint.color = Color.RED
            paint.strokeWidth = 10f
        }

        override fun run() {
            var canvas: Canvas?
            var x1: Float = -carHalfWidth
            var y1: Float = carHalfHeight
            var x2: Float = carHalfWidth
            var y2: Float = carHalfHeight
            var x3: Float = carHalfWidth
            var y3: Float = -carHalfHeight
            var x4: Float = -carHalfWidth
            var y4: Float = -carHalfHeight
            while (running) {

                val now = System.currentTimeMillis()
                val elapsedTime = now - prevTime

                if (elapsedTime > DELTA_TIME) {
                    prevTime = now
                    try {
                        movementCoordinates
                            .poll()
                            ?.let {
                                currentX += it.first
                                currentY += it.second
                                currentAngle = it.third
                                x1 =
                                    halfDiagonal.toFloat() * cos(it.third + diagonalAngle).toFloat()
                                y1 =
                                    halfDiagonal.toFloat() * sin(it.third + diagonalAngle).toFloat()
                                x2 =
                                    halfDiagonal.toFloat() * cos(it.third - diagonalAngle).toFloat()
                                y2 =
                                    halfDiagonal.toFloat() * sin(it.third - diagonalAngle).toFloat()
                                x3 = -x1
                                y3 = -y1
                                x4 = -x2
                                y4 = -y2
                            }
                    } catch (e: EmptyStackException) {
                        // we done here
                    }
                }
                canvas = null
                try {
                    canvas = surfaceHolder.lockCanvas(null)
                    synchronized(surfaceHolder) {
                        canvas.drawColor(Color.WHITE)
                        canvas.drawLine(
                            currentX + x1,
                            currentY + y1,
                            currentX + x2,
                            currentY + y2,
                            paint
                        )
                        canvas.drawLine(
                            currentX + x2,
                            currentY + y2,
                            currentX + x3,
                            currentY + y3,
                            paint
                        )
                        canvas.drawLine(
                            currentX + x3,
                            currentY + y3,
                            currentX + x4,
                            currentY + y4,
                            paint
                        )
                        canvas.drawLine(
                            currentX + x4,
                            currentY + y4,
                            currentX + x1,
                            currentY + y1,
                            paint
                        )
                    }
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }

        fun addCoordinatesTriple(xyAngleTriple: Triple<Float, Float, Float>) {
            movementCoordinates.add(xyAngleTriple)
        }

        fun addAllCoordinates(coordinates: Collection<Triple<Float, Float, Float>>) {
            movementCoordinates.clear()
            movementCoordinates.addAll(coordinates)
        }

        companion object {
            const val DELTA_TIME = 30
        }
        /*--
        V1 solution:
        car can move V pixels per second ->
        in that case if destination == Xd ->
        Xd-X / V -> seconds will takes this movement

        * */
    }
}

