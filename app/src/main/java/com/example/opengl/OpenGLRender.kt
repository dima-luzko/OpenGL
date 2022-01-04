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

    companion object {
        private const val POSITION_COUNT = 3
//        private const val TEXTURE_COUNT = 2
//        private const val STRIDE = (POSITION_COUNT
//                + TEXTURE_COUNT) * 4
    }

    private var programId = 0
    private var texture = 0
    private var vertexData: FloatBuffer? = null
    // параметры шейдеров
    private var aPositionLocation = 0
    private var aTextureLocation = 0
    private var uTextureUnitLocation = 0
    private var uMatrixLocation = 0
    // координаты смещения модели
    private var mX = 0f
    private var mY = 0f
    // матрицы преобразований
    private val mProjectionMatrix = FloatArray(16) // Проекционная матрица
    private val mViewMatrix = FloatArray(16) // View матрица
    private val mMatrix = FloatArray(16)   // Итоговая матрица
    private val mModelMatrix = FloatArray(16) // Model матрица
    private val tempMatrix = FloatArray(16)
    private var textureArray = IntArray(16)

    private var indexArray: ByteBuffer? = null

    @Volatile
    var angle: Float = 0f


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
       textureArray = intArrayOf(R.drawable.box0,R.drawable.box1,
            R.drawable.box2,R.drawable.box3,
            R.drawable.box4,R.drawable.box5)

        val vertices = floatArrayOf(
//            //back
//            -1.0f, 1.0f, -1.0f, 1f, 1f,
//            1.0f, 1.0f, -1.0f, 0f, 1f,
//            -1.0f, -1.0f, -1.0f, 1f, 0f,
//            1.0f, -1.0f, -1.0f, 0f, 0f,
//
//            //front
//            -1.0f, 1.0f, 1.0f, 0f, 1f,
//            1.0f, 1.0f, 1.0f, 1f, 1f,
//            -1.0f, -1.0f, 1.0f, 0f, 0f,
//            1.0f, -1.0f, 1.0f, 1f, 0f,
//
//            //left
//            -1.0f, 1.0f, -1.0f, 0f, 1f,
//            -1.0f, -1.0f, -1.0f, 0f, 0f,
//            -1.0f, -1.0f, 1.0f, 1f, 0f,
//            -1.0f, 1.0f, 1.0f, 1f, 1f,
//
//
//            //right
//            1.0f, 1.0f, -1.0f, 1f, 1f,
//            1.0f, -1.0f, -1.0f, 1f, 0f,
//            1.0f, -1.0f, 1.0f, 0f, 0f,
//            1.0f, 1.0f, 1.0f, 0f, 1f,
//
//            //top
//            -1.0f, 1.0f, -1.0f, 0f, 1f,
//            -1.0f, 1.0f, 1.0f, 0f, 0f,
//            1.0f, 1.0f, 1.0f, 1f, 0f,
//            1.0f, 1.0f, -1.0f, 1f, 1f,
//
//            //bottom
//            -1.0f, -1.0f, -1.0f, 0f, 0f,
//            -1.0f, -1.0f, 1.0f, 0f, 1f,
//            1.0f, -1.0f, 1.0f, 1f, 1f,
//            1.0f, -1.0f, -1.0f, 1f, 0f

//            //back
//            -1.0f, 1.0f, -1.0f,
//            1.0f, 1.0f, -1.0f,
//            -1.0f, -1.0f, -1.0f,
//            1.0f, -1.0f, -1.0f,
//
//            //front
//            -1.0f, 1.0f, 1.0f,
//            1.0f, 1.0f, 1.0f,
//            -1.0f, -1.0f, 1.0f,
//            1.0f, -1.0f, 1.0f,
//
//            //left
//            -1.0f, 1.0f, -1.0f,
//            -1.0f, -1.0f, -1.0f,
//            -1.0f, -1.0f, 1.0f,
//            -1.0f, 1.0f, 1.0f,
//
//
//            //right
//            1.0f, 1.0f, -1.0f,
//            1.0f, -1.0f, -1.0f,
//            1.0f, -1.0f, 1.0f,
//            1.0f, 1.0f, 1.0f,
//
//            //top
//            -1.0f, 1.0f, -1.0f,
//            -1.0f, 1.0f, 1.0f,
//            1.0f, 1.0f, 1.0f,
//            1.0f, 1.0f, -1.0f,
//
//            //bottom
//            -1.0f, -1.0f, -1.0f,
//            -1.0f, -1.0f, 1.0f,
//            1.0f, -1.0f, 1.0f,
//            1.0f, -1.0f, -1.0f

            -1f,  1f,  1f,
            1f,  1f,  1f,
            -1f, -1f,  1f,
            1f, -1f,  1f,
            -1f,  1f, -1f,
            1f,  1f, -1f,
            -1f, -1f, -1f,
            1f, -1f, -1f
        )
        vertexData = ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)

        indexArray = ByteBuffer.allocateDirect(36)
            .put(
                byteArrayOf( // грани куба
                    // ближняя
                    1, 3, 0,
                    0, 3, 2,
                    // дальняя
                    4, 6, 5,
                    5, 6, 7,
                    // левая
                    0, 2, 4,
                    4, 2, 6,
                    // правая
                    5, 7, 1,
                    1, 7, 3,
                    // верхняя
                    5, 1, 4,
                    4, 1, 0,
                    // нижняя
                    6, 2, 7,
                    7, 2, 3
                )
            )

        indexArray?.position(0)

        texture = TextureUtils().loadTexture(context, textureArray )
    }

    /** получение координаты нажатия х  */
    fun getX(): Float {
        return mX
    }

    /** установление координаты нажатия x  */
    fun setX(x: Float) {
        var setX = x
        if (setX > 360) setX = 0f else if (setX < 0) setX = 360f
        mX = setX
    }

    /** получение координаты нажатия y  */
    fun getY(): Float {
        return mY
    }

    /** установление координаты нажатия y  */
    fun setY(y: Float) {
        var setY = y
        if (setY > 360) setY = 0f else if (setY < 0) setY = 360f
        mY = setY
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
        uTextureUnitLocation = glGetUniformLocation(programId, "u_TextureUnit")
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix")
    }

    private fun bindData() {
        // координаты вершин
        vertexData!!.position(0)
        glVertexAttribPointer(
            aPositionLocation, POSITION_COUNT, GL_FLOAT,
            false, 0, vertexData
        )
        glEnableVertexAttribArray(aPositionLocation)

//        // координаты текстур
//        vertexData!!.position(0)
//        glVertexAttribPointer(
//            aTextureLocation, TEXTURE_COUNT, GL_FLOAT,
//            false, 0, vertexData
//        )
//        glEnableVertexAttribArray(aTextureLocation)

        // помещаем текстуру в target CUBE_MAP юнита 0
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture)

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

    }

    private fun createViewMatrix() {
        // точка положения камеры
        val eyeX = 0f
        val eyeY = 0f
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

    private fun bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mMatrix, 0)
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0)
    }


    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        //glDrawArrays(GL_TRIANGLES, texture, 24)

        //glDrawElements(GL_TRIANGLES,24,GL_UNSIGNED_BYTE,0)
        Matrix.setIdentityM(mModelMatrix, 0)
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray);
        rotate()
    }

    private fun rotate() {
        //Matrix.setIdentityM(mModelMatrix, 0)
        Matrix.rotateM(mModelMatrix, 0, mX, 0.0f, 1.0f, 0.0f)
        Matrix.rotateM(mModelMatrix, 0, mY, 1.0f, 0.0f, 0.0f)
        Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        bindMatrix()
    }


}