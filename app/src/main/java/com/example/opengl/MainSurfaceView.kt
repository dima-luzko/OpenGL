package com.example.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

class MainSurfaceView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private var glRender : OpenGLRender? = null

    companion object {
        private const val TOUCH_SCALE_FACTOR = 180.0f / 320f
    }

    private var previousX: Float = 0f
    private var previousY: Float = 0f


    init {
        init()
        renderMode = RENDERMODE_WHEN_DIRTY
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

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {

                var dx: Float = x - previousX
                var dy: Float = y - previousY

                // reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx *= -1
                }

                // reverse direction of rotation to left of the mid-line
                if (x < width / 2) {
                    dy *= -1
                }

                glRender!!.angle += (dx + dy) * TOUCH_SCALE_FACTOR
                requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }
}