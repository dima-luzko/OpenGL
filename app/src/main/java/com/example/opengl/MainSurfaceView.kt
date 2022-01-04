package com.example.opengl

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

class MainSurfaceView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
    companion object {
        private const val TOUCH_SCALE_FACTOR = 90.0f / 320f
        private const val TOUCH_SCALE_FACTOR_X = 90.0f / 320
        private const val TOUCH_SCALE_FACTOR_Y = 45.0f / 320
    }

    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private var glRender: OpenGLRender? = null

    init {
        initRender()
    }

    /** Метод инициализации рендера **/
    private fun initRender() {
        setEGLContextClientVersion(2)
        setZOrderOnTop(true)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.RGBA_8888)
        glRender = OpenGLRender(context)
        setRenderer(glRender)
        //renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx: Float = x - previousX
                val dy: Float = y - previousY
                val oldX = glRender!!.getX()
                val oldY = glRender!!.getY()
                glRender!!.setX(oldX + dx * TOUCH_SCALE_FACTOR_X)
                if (oldX in 90.0..280.0) {
                    glRender!!.setY(oldY - dy * TOUCH_SCALE_FACTOR_Y)
                    Log.d("LOX","rotate_up")
                } else {
                    glRender!!.setY(oldY + dy * TOUCH_SCALE_FACTOR_Y)
                    Log.d("LOX","rotate_down")
                }

//                glRender!!.angle += (dx + dy) * TOUCH_SCALE_FACTOR
                //   requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }
}