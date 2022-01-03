package com.example.opengl


import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class OpenGLRender(private val context: Context) : GLSurfaceView.Renderer {


    private val POSITION_COUNT = 3
    private val TEXTURE_COUNT = 2
    private val TIME = 4000L
    private val STRIDE = (POSITION_COUNT
            + TEXTURE_COUNT) * 4


    private var vertexData: FloatBuffer? = null


    private var aPositionLocation = 0
    private var aTextureLocation = 0
    private var uTextureUnitLocation = 0
    private var uMatrixLocation = 0

    private var programId = 0

    private val mProjectionMatrix = FloatArray(16) // Проекционная матрица
    private val mViewMatrix = FloatArray(16) // View матрица
    private val mMatrix = FloatArray(16)   // Итоговая матрица
    private val mModelMatrix = FloatArray(16) // Model матрица

    private var texture = 0

    var centerX = 0f
    var centerY = 0f


    private var angle: Float = 0f


    fun setAngle(angle: Float) {
        this.angle = angle
    }



    override fun onSurfaceCreated(arg0: GL10?, arg1: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 1f)
        glEnable(GL_DEPTH_TEST)
        createAndUseProgram()
        getLocations()
        prepareData()
        bindData()
        createViewMatrix()
        Matrix.setIdentityM(mModelMatrix, 0)
    }

    override fun onSurfaceChanged(arg0: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        createProjectionMatrix(width, height)
        bindMatrix()
    }

    private fun prepareData() {
        val vertices = floatArrayOf(
            //back
            -1.0f, 1.0f, -1.0f,   1f,1f,
            1.0f, 1.0f, -1.0f,    0f,1f,
            -1.0f, -1.0f, -1.0f,  1f,0f,
            1.0f, -1.0f, -1.0f,   0f,0f,

            //front
            -1.0f, 1.0f, 1.0f,    0f,1f,
            1.0f, 1.0f, 1.0f,     1f,1f,
            -1.0f, -1.0f, 1.0f,   0f,0f,
            1.0f, -1.0f, 1.0f,    1f,0f,

            //left
            -1.0f, 1.0f, -1.0f,   0f,1f,
            -1.0f, -1.0f, -1.0f,  0f,0f,
            -1.0f, -1.0f, 1.0f,   1f,0f,
            -1.0f, 1.0f, 1.0f,    1f,1f,


            //right
            1.0f, 1.0f, -1.0f,    1f,1f,
            1.0f, -1.0f, -1.0f,   1f,0f,
            1.0f, -1.0f, 1.0f,    0f,0f,
            1.0f, 1.0f, 1.0f,     0f,1f,

            //top
            -1.0f, 1.0f, -1.0f,   0f,1f,
            -1.0f, 1.0f, 1.0f,    0f,0f,
            1.0f, 1.0f, 1.0f,     1f,0f,
            1.0f, 1.0f, -1.0f,    1f,1f,

            //bottom
            -1.0f, -1.0f, -1.0f,  0f,0f,
            -1.0f, -1.0f, 1.0f,   0f,1f,
            1.0f, -1.0f, 1.0f,    1f,1f,
            1.0f, -1.0f, -1.0f,    1f,0f
        )
        vertexData = ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        texture = TextureUtils().loadTexture(context, R.drawable.boxs)
    }

    private fun createAndUseProgram() {
        val vertexSharedId =
            ProgramUtils().createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shared)
        val fragmentSharedId =
            ProgramUtils().createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader)
        programId = ProgramUtils().createProgram(vertexSharedId, fragmentSharedId)
        glUseProgram(programId)
    }

    private fun getLocations() {
        aPositionLocation = glGetAttribLocation(programId, "a_Position")
        aTextureLocation = glGetAttribLocation(programId, "a_Texture")
        uTextureUnitLocation = glGetUniformLocation(programId, "u_TextureUnit")
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix")
    }

    private fun bindData() {
        // координаты вершин
        vertexData!!.position(0)
        glVertexAttribPointer(
            aPositionLocation, POSITION_COUNT, GL_FLOAT,
            false, STRIDE, vertexData
        )
        glEnableVertexAttribArray(aPositionLocation)

        // координаты текстур
        vertexData!!.position(POSITION_COUNT)
        glVertexAttribPointer(
            aTextureLocation, TEXTURE_COUNT, GL_FLOAT,
            false, STRIDE, vertexData
        )
        glEnableVertexAttribArray(aTextureLocation)

        // помещаем текстуру в target 2D юнита 0
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texture)

        // юнит текстуры
        glUniform1i(uTextureUnitLocation, 0)
    }

    private fun createProjectionMatrix(width: Int, height: Int) {
        var ratio = 1f
        var left = -1f
        var right = 1f
        var bottom = -1f
        var top = 1f
        val near = 2f
        val far = 12f
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

        centerX = (width/2).toFloat()
        centerY = (height/2).toFloat()
    }

    private fun createViewMatrix() {
        // точка положения камеры
        val eyeX = 0f
        val eyeY = 2f
        val eyeZ = 4f

        // точка направления камеры
        val centerX = 0f
        val centerY = 0f
        val centerZ = 0f

        // up-вектор
        val upX = 0f
        val upY = 1f
        val upZ = 0f
        Matrix.setLookAtM(
            mViewMatrix,
            0,
            eyeX,
            eyeY,
            eyeZ,
            centerX,
            centerY,
            centerZ,
            upX,
            upY,
            upZ
        )
    }

    private fun matrixRotateX(angle: Float){
        val tempMatrix = FloatArray(16)
        Matrix.rotateM(mModelMatrix,0,angle,0f,-1f,0f)
        Matrix.multiplyMM(tempMatrix,0,mMatrix,0,mViewMatrix,0)
        bindMatrix()
    }

    private fun bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mMatrix, 0)
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0)
    }

    override fun onDrawFrame(arg0: GL10?) {
        val angle = 0.090f * TIME
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 24)
        //matrixRotateX(angle)
        //setModelMatrix()
    }

    private fun setModelMatrix() {
        val time = (SystemClock.uptimeMillis() % TIME).toFloat()
        val angle = 0.090f * time
        Matrix.rotateM(mModelMatrix, 0, angle, 0f, 1f, 0f)
        bindMatrix()
    }





}