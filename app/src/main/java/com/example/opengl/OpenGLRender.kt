package com.example.opengl


import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import androidx.renderscript.Float3
import androidx.renderscript.Float4
import com.example.opengl.data.ModelComponent
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.sqrt


class OpenGLRender(private val context: Context) : GLSurfaceView.Renderer {

    companion object {
        private const val POSITION_COUNT = 3
    }
    private var programId = 0
    private var texture = 0
    private var buffer: FloatBuffer? = null
    private var indexArray: ByteBuffer? = null
    private var textureArrayFirstModel = IntArray(16)
    private var textureArraySecondModel = IntArray(16)

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

    @Volatile
    var angle: Float = 0f


    override fun onSurfaceCreated(arg0: GL10?, arg1: EGLConfig?) {
        glClearColor(0f, 1f, 0f, 1f)
        glEnable(GL_DEPTH_TEST)
        createAndUseProgram()
        getLocations()
        setTexture()
        createViewMatrix()
        Matrix.setIdentityM(mModelMatrix, 0)
    }

    override fun onSurfaceChanged(arg0: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        createProjectionMatrix(width, height)
        bindMatrix()
    }

    fun vertexArray(vertexData: FloatArray) {
        buffer = ByteBuffer
            .allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)
    }

    fun getVertexAttribute(
        attributeLocation: Int,
        componentCount: Int,
        texture: Int,
        textureLocation: Int
    ) {
        // координаты вершин
        buffer!!.position(0)
        glVertexAttribPointer(
            attributeLocation, componentCount, GLES20.GL_FLOAT,
            false, 0, buffer
        )
        GLES20.glEnableVertexAttribArray(attributeLocation)
        // помещаем текстуру в target CUBE_MAP юнита 0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, texture)
        // юнит текстуры
        GLES20.glUniform1i(textureLocation, 0)
    }

    private fun bindFirstModel() {
        textureArrayFirstModel = ModelComponent.createTexture(
            R.drawable.box0,
            R.drawable.box1,
            R.drawable.box2,
            R.drawable.box3,
            R.drawable.box4,
            R.drawable.box5
        )
        texture = TextureUtils().loadTexture(context, textureArrayFirstModel)
        vertexArray(ModelComponent.verticesFirstModel)
        getVertexAttribute(
            aPositionLocation,
            POSITION_COUNT,
            texture,
            uTextureUnitLocation
        )
    }

    private fun bindSecondModel() {
        textureArraySecondModel = ModelComponent.createTexture(
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo
        )
        texture = TextureUtils().loadTexture(context, textureArraySecondModel)
        vertexArray(ModelComponent.verticesSecondModel)
        getVertexAttribute(
            aPositionLocation,
            POSITION_COUNT,
            texture,
            uTextureUnitLocation
        )
    }

    private fun setTexture() {
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
    }

    /** получение координаты нажатия х **/
    fun getX(): Float {
        return mX
    }

    /** установление координаты нажатия x **/
    fun setX(x: Float) {
        var setX = x
        if (setX > 360) setX = 0f else if (setX < 0) setX = 360f
        mX = setX
    }

    /** получение координаты нажатия y **/
    fun getY(): Float {
        return mY
    }

    /** установление координаты нажатия y **/
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
        val eyeZ = 6f

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



    private fun addElementToArray(
        arr: Array<Pair<Float, Int>>,
        element1: Float,
        element2: Int
    ): Array<Pair<Float, Int>> {
        val newArray = arr.toMutableList()
        newArray.add(Pair(element1, element2))
        return newArray.toTypedArray()
    }

    private fun addElementToArray(arr: Array<Float>, element1: Float): Array<Float> {
        val newArray = arr.toMutableList()
        newArray.add(element1)
        return newArray.toTypedArray()
    }

    private fun addElementToArray (arr: Array<Int>,element: Array<Int>) : Array<Int>{
        val newArray = arr.toMutableList()
        newArray.addAll(element)
        return newArray.toTypedArray()
    }

    private fun buildRenderIndices(coords: Array<ModelComponent.Vertex>): Array<Int> {
        val cubeVertsCount = 36
        val rectVertsCount = 6
        val maxDrsCubes: Array<Float> = emptyArray()
        var maxDrCube = 0.0f
        val maxDrsRects: Array<Pair<Float, Int>> = emptyArray()
        var maxDrRect = 0.0f

        var cubeIdx = 0
        for (i in 0..coords.count()) {
            val pos = coords[i].pos
            val w = coords[i].pos.w
            val nextPos = Float3(pos.x / w, pos.y / w, pos.z / w)
            val zero = Float3(0.0f, 0.0f, -1000.0f)
            val dr = sqrt(zero.x - nextPos.x) + sqrt(zero.y - nextPos.y) + sqrt(zero.z - nextPos.z)

            maxDrRect = max(dr, maxDrRect)
            maxDrCube = max(dr, maxDrCube)

            if ((i + 1) % rectVertsCount == 0) {
                addElementToArray(maxDrsRects, maxDrRect, cubeIdx)
                maxDrRect = 0.0f
            }

            if ((i + 1) % cubeVertsCount == 0) {
                addElementToArray(maxDrsCubes, maxDrCube)
                cubeIdx += 1
            }
        }

        val _res: Array<Int> = (0..maxDrsRects.count()).toList().toTypedArray()
        _res.sortBy{
            maxDrsCubes[maxDrsRects[it].second]
        }

        val res: Array<Int> = emptyArray()
        for (chunk in _res.toList().chunked(6)) {
            chunk.toTypedArray().sortBy {
                maxDrsRects[it].first
            }
            addElementToArray(res,chunk.toTypedArray())
        }

        return res
    }

    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        Matrix.setIdentityM(mModelMatrix, 0)

        rotate()

        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)

        bindSecondModel()
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray)

        bindFirstModel()
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray)

        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CW)

        bindFirstModel()
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray)

        bindSecondModel()
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray)

    }

    private fun rotate() {
        Matrix.rotateM(mModelMatrix, 0, mX, 0.0f, 1.0f, 0.0f)
        // Matrix.setRotateM(mModelMatrix,0,mX,0.0f,1.0f,0.0f)
        Matrix.rotateM(mModelMatrix, 0, mY, 1.0f, 0.0f, 0.0f)
        Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        bindMatrix()
    }
}