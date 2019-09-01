package com.example.uikit

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.utils.dLog


class CarMapView : View {
    var carX: Float = 0f
    var carY: Float = 0f
    var finishX: Float = 0f
    var finishY: Float = 0f
    var angle = 30f
    var car: Bitmap? = null
    var carMiddleX = 0f
    var carMiddleY = 0f

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context, attrs)
    }

    private fun initView(
        context: Context,
        attrs: AttributeSet?
    ) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                it, R.styleable.CarMapView, 0, 0
            )
            carX = typedArray.getFloat(R.styleable.CarMapView_car_x, 0f)
            carY = typedArray.getFloat(R.styleable.CarMapView_car_y, 0f)
            finishX = typedArray.getFloat(R.styleable.CarMapView_car_y, 0f)
            finishY = typedArray.getFloat(R.styleable.CarMapView_finish_y, 0f)
            angle = typedArray.getFloat(R.styleable.CarMapView_car_angle, 0f)

            typedArray.recycle()
        }
        car = BitmapFactory
            .decodeResource(resources, R.drawable.car)
            ?.apply {
                "width = $width carMiddleX = $carMiddleX".dLog()
                carMiddleX = width / 2f
                carMiddleY = height / 2f
            }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawCar(canvas)
    }

    private fun drawCar(canvas: Canvas?) {
        canvas?.apply {
            //x' = cos(angle of rotation) * x - sin(angle of rotation) * y
            //y' = cos(angle of rotation) * y + sin(angle of rotation) * x
//            val cosA = cos(angle)
//            val sinA = sin(angle)
//            val x1 = (cosA * (carX - 50) - sinA * (carY - 100)).toFloat()
//            val y1 = (cosA * (carY - 100) - sinA * (carX - 50)).toFloat()
//
//            val x2 = (cosA * (carX + 50) - sinA * (carY + 100)).toFloat()
//            val y2 = (cosA * (carY + 100) - sinA * (carX + 50)).toFloat()
//
            val x1 = carX - 50
            val y1 = carY - 100

            val x2 = carX + 50
            val y2 = carY + 100

            //val drawable = resources.getDrawable(R.drawable.car_top_view, null)
            val matrix = Matrix()
            matrix.setRotate(angle)
            val paint = Paint().apply {
                color = Color.RED
            }
            car?.apply {
                val rotatedCar = Bitmap.createBitmap(
                    this,
                    -this.width / 2, -this.height / 2,
                    this.width,
                    this.height,
                    matrix,
                    true
                )
                drawBitmap(
                    rotatedCar,
                    carX - carMiddleX,
                    carY - carMiddleY,
                    paint
                )
                "carX = $carX carMiddleX = $carMiddleX this.width = ${this.width}".dLog()

            }
//
//            val rect = RectF(x1, y1, x2, y2)
//            matrix.mapRect(rect)
//            //matrix.mapRect(rect)
//            drawRect(rect, paint)
        }
    }
}