package com.example.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import kotlin.math.atan2

class MainSurfaceView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private var glRender : OpenGLRender? = null

    companion object {
        private const val TOUCH_SCALE_FACTOR = 180.0f / 320f
    }

    private var previousX: Float = 0f
    private var previousY: Float = 0f


    init {
        init()
    }

    /** Метод инициализации рендера **/
    private fun init () {
        setEGLContextClientVersion(2)
        glRender = OpenGLRender(context)
        setRenderer(glRender)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = event.x
        val y = event.y

        when (event.action){
            MotionEvent.ACTION_MOVE -> {
                val angleRadian = atan2(y - glRender!!.centerY, x - glRender!!.centerX)
                glRender?.setAngle(Math.toDegrees((-angleRadian.toDouble())).toFloat())
                requestRender()
            }
        }

        return true
    }
}