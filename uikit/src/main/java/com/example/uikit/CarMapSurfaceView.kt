package com.example.uikit

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.content.res.Resources
import android.graphics.BitmapFactory
import com.example.utils.dLog
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.abs


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
        //   drawThread?.finishAngle = angle
    }

    fun getCarX() = drawThread?.currentX
    fun getCarY() = drawThread?.currentY
    fun getAngle() = drawThread?.currentAngle

    fun addCoordinatesTriple(xyPair: Triple<Float,Float, Float>) {
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

    fun updateCoordinates(x: Float, y: Float) {
        drawThread?.updateFinishCoordinatesV1(x, y)
    }

    internal class DrawThread(
        private val surfaceHolder: SurfaceHolder, resources: Resources
    ) : Thread() {
        var finishAngle = 30f
        var currentAngle = 0f

        var running = true

        var finishX = 0f
        var finishY = 0f
        var currentX = 0f
        var currentY = 0f

        private val movementCoordinates = ConcurrentLinkedQueue<Triple<Float, Float, Float>>()

        private val picture = BitmapFactory.decodeResource(resources, R.drawable.car)
        private val pictureWidth = (picture.width / 2).toFloat()
        private val pictureHeight = (picture.height / 2).toFloat()

        private val matrix = Matrix()
        private var prevTime = 0L


        init {
            matrix.setScale(.1f, .1f)
            matrix.setTranslate(.0f, .0f)
            prevTime = System.currentTimeMillis()
        }

        override fun run() {
            var canvas: Canvas?
            while (running) {

                val now = System.currentTimeMillis()
                val elapsedTime = now - prevTime
                if (elapsedTime > DELTA_TIME) {
                    prevTime = now
                    if (abs(abs(currentAngle) - abs(finishAngle)) > 2f) {
                        " run currentAngle = $currentAngle finishAngle = $finishAngle".dLog()
                        currentAngle += if (currentAngle > finishAngle) -DELTA_ANGLE else DELTA_ANGLE
                        matrix.preRotate(
                            if (currentAngle > finishAngle) -DELTA_ANGLE else DELTA_ANGLE,
                            pictureWidth,
                            pictureHeight
                        )
                    } else {
                        try {
                            movementCoordinates
                                .poll()
                                ?.let {
                                    matrix.setTranslate(it.first, it.second)
                                    matrix.setRotate(it.third)
                                    currentX = it.first
                                    currentY = it.second
                                }

                        } catch (e: EmptyStackException) {
                            // we done here
                        }

//                        if ((abs(abs(currentX) - abs(finishX)) > 10f) || (abs(
//                                abs(currentY) - abs(
//                                    finishY
//                                )
//                            ) > 10f)
//                        ) {
//                            currentX += if (currentX > finishX) -DELTA_COORDINATES else DELTA_COORDINATES
//                            currentY += if (currentY > finishY) -DELTA_COORDINATES else DELTA_COORDINATES
//                            matrix.preTranslate(
//                                if (currentX > finishX) -DELTA_COORDINATES else DELTA_COORDINATES,
//                                if (currentY > finishY) -DELTA_COORDINATES else DELTA_COORDINATES
//                            )
//                        }
                    }
                }


                canvas = null
                try {
                    canvas = surfaceHolder.lockCanvas(null)
                    synchronized(surfaceHolder) {
                        canvas.drawColor(Color.BLACK)
                        canvas.drawBitmap(picture, matrix, null)
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
            movementCoordinates.addAll(coordinates)
        }

        fun updateFinishCoordinatesV1(x: Float, y: Float) {
            finishX = x
            finishY = y
            "updateFinishCoordinatesV1 currentX = $currentX currentY = $currentY".dLog()
            "updateFinishCoordinatesV1 finishX = $finishX finishY = $finishY".dLog()

            //   matrix.setTranslate(x - pictureWidth, y - pictureHeight)
        }

        companion object {
            const val DELTA_TIME = 30
            const val DELTA_ANGLE = 2f
            const val DELTA_COORDINATES = 5f

        }
        /*--
        V1 solution:
        car can move V pixels per second ->
        in that case if destination == Xd ->
        Xd-X / V -> seconds will takes this movement

        * */
    }
}

