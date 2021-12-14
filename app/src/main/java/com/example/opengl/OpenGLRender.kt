package com.example.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRender(private val context: Context) : GLSurfaceView.Renderer {

    private var vertexData: FloatBuffer? = null
    private var programId = 0
    private var uColorLocation = 0
    private var aPositionLocation = 0

    init {
        prepareData()
    }

    override fun onSurfaceCreated(gl: GL10?, eglConfig: EGLConfig?) {
        gl?.glClearColor(0f, 0f, 0f, 1f)
        val fragmentShader =
            context.let { FileGLRawReader().readTextFromRaw(it, R.raw.fragment_shader) }
        val vertexShader =
            context.let { FileGLRawReader().readTextFromRaw(it, R.raw.vertex_shared) }
        val vertexSharedId =
            ProgramUtils().createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shared)
        val fragmentSharedId =
            ProgramUtils().createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader)
        programId = ProgramUtils().createProgram(vertexSharedId, fragmentSharedId)
        glUseProgram(programId)
        bindData()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        gl?.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)

        glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f)
        glDrawArrays(GL_TRIANGLES, 4, 3)
    }

    private fun prepareData() {
        val vertices = floatArrayOf(


            // треугольник 3
            0.1f, 0.8f, 0.1f, 0.2f, 0.5f, 0.8f, 0.5f, 0.2f,

            // треугольник 1
            -0.9f, 0.8f,
            -0.9f, 0.2f,
            -0.5f, 0.8f,

//            // треугольник 4
//                 0.5f, 0.2f, 0.5f, 0.8f,

        )
        vertexData = ByteBuffer.allocateDirect(vertices.size * 4).order(ByteOrder.nativeOrder())
            .asFloatBuffer().put(vertices)
    }

    private fun bindData() {
        uColorLocation = glGetUniformLocation(programId, "u_Color")

        aPositionLocation = glGetAttribLocation(programId, "a_Position")
        vertexData?.position(0)
        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 0, vertexData)
        glEnableVertexAttribArray(aPositionLocation)
    }
}