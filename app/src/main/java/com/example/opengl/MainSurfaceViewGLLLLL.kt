package com.example.opengl

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

/** компонент в котором отрисовывается 3d модель  */
//class MainSurfaceViewGLLLLL : GLSurfaceView {
//    private val TOUCH_SCALE_FACTOR_X = 90.0f / 320
//    private val TOUCH_SCALE_FACTOR_Y = 45.0f / 320
//    private var mPreviousX = 0f
//    private var mPreviousY = 0f
//    private var mRenderer: MainRenderer? = null
//
////    constructor(context: Context?) : super(context) {
////        init()
////    }
////
////    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
////        init()
////    }
//
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        val x: Float = event.x
//        val y: Float = event.y
//        when (event.action) {
//            MotionEvent.ACTION_MOVE -> {
//                val dx = x - mPreviousX
//                val dy = y - mPreviousY
//                val oldX: Float = mRenderer.getX()
//                mRenderer.setX(oldX + dx * TOUCH_SCALE_FACTOR_X)
//                if (oldX in 90.0..280.0) mRenderer.setY(mRenderer.getY() - dy * TOUCH_SCALE_FACTOR_Y) else mRenderer.setY(
//                    mRenderer.getY() + dy * TOUCH_SCALE_FACTOR_Y
//                )
//            }
//        }
//        mPreviousX = x
//        mPreviousY = y
//        return true
//    }
//
//    /** установление скина для модели  */
//    fun setBaseSkin(skinBitmap: Bitmap?) {
//        queueEvent(Runnable { mRenderer.setSkinBitmap(skinBitmap) })
//    }
//
//    /** сброс видимости частей модели скина  */
//    fun resetVisibility() {
//        queueEvent(Runnable { mRenderer.resetVisibility() })
//    }
//    /** получение режима отображения  */
//    /** установление режима отображения  */
//    var viewType: TypeSide
//        get() = mRenderer.getViewType()
//        set(viewType) {
//            when (viewType) {
//                FRONT -> {
//                    mRenderer.setX(0)
//                    mRenderer.setY(0)
//                }
//                RIGHT -> {
//                    mRenderer.setX(90)
//                    mRenderer.setY(0)
//                }
//                BACK -> {
//                    mRenderer.setX(180)
//                    mRenderer.setY(0)
//                }
//                LEFT -> {
//                    mRenderer.setX(270)
//                    mRenderer.setY(0)
//                }
//                TOP -> {
//                    mRenderer.setX(0)
//                    mRenderer.setY(90)
//                }
//                BOTTOM -> {
//                    mRenderer.setX(0)
//                    mRenderer.setY(270)
//                }
//            }
//        }
//
//    /** установление видимости частей модели скина  */
//    fun setVisibilityParts(visibilityParts: Map<TypePart?, Boolean?>?) {
//        mRenderer.setVisibilityParts(visibilityParts)
//    }
//
//    /** метод инициализации  */
//    private fun init() {
//        setEGLContextClientVersion(2)
//        setZOrderOnTop(true)
//        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
//        holder.setFormat(PixelFormat.RGBA_8888)
//        mRenderer = MainRenderer(context)
//        setRenderer(mRenderer)
//    }
//}