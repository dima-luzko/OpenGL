package com.example.opengl

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
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
    private var uMatrixLocation = 0

    private val mProjectionMatrix = FloatArray(16)

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
        glEnable(GL_DEPTH_TEST)
        gl?.glViewport(0, 0, width, height)
        bindMatrix(width, height);
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        // зеленый треугольник
        glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f)
        glDrawArrays(GL_TRIANGLES, 0, 3)

        // синий треугольник
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f)
        glDrawArrays(GL_TRIANGLES, 3, 3)
    }

    private fun prepareData() {
        val z1 = -1.0f
        val z2 = -1.0f

        val vertices = floatArrayOf(
// первый треугольник
            -0.7f, -0.5f, z1,
            0.3f, -0.5f, z1,
            -0.2f, 0.3f, z1,

            // второй треугольник
            -0.3f, -0.4f, z2,
            0.7f, -0.4f, z2,
            0.2f, 0.4f, z2,
        )

        vertexData = ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
    }

    private fun bindData() {
        uColorLocation = glGetUniformLocation(programId, "u_Color")
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");
        aPositionLocation = glGetAttribLocation(programId, "a_Position")
        vertexData?.position(0)
        glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0, vertexData)
        glEnableVertexAttribArray(aPositionLocation)
    }

    private fun bindMatrix(width: Int, height: Int) {
        var ratio = 1.0f
        var left = -1.0f
        var right = 1.0f
        var bottom = -1.0f
        var top = 1.0f
        val near = 1.0f
        val far = 8.0f

        if (width > height) {
            ratio = width.toFloat() / height
            left *= ratio
            right *= ratio
        } else {
            ratio = height.toFloat() / width
            bottom *= ratio
            top *= ratio
        }

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far)
        glUniformMatrix4fv(uMatrixLocation, 1, false, mProjectionMatrix, 0)
    }
}