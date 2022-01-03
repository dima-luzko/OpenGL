package com.example.opengl.test

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer: GLSurfaceView.Renderer {

    companion object {
        private const val COORDS_PER_VERTEX = 3
    }

    @Volatile
    var angle: Float = 0f

    private lateinit var mTriangle: Triangle
    private lateinit var mSquare: Square

    var triangleCoords = floatArrayOf(     // in counterclockwise order:
        0.0f, 0.622008459f, 0.0f,      // top
        -0.5f, -0.311004243f, 0.0f,    // bottom left
        0.5f, -0.311004243f, 0.0f      // bottom right
    )

    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)




    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // initialize a triangle
        mTriangle = Triangle()
        // initialize a square
        mSquare = Square()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(p0: GL10?) {
        val scratch = FloatArray(16)

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

//        val time = SystemClock.uptimeMillis() % 4000L
//        val angle = 0.090f * time.toInt()
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, -1f, 0f)

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        Triangle().draw(scratch)
    }


    class Triangle {
        private var mProgram: Int
        private var vPMatrixHandle: Int = 0
        private val vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}"

        private val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"

        private val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)
        private var vertexBuffer: FloatBuffer =
            // (number of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(MyGLRenderer().triangleCoords.size * 4).run {
                // use the device hardware's native byte order
                order(ByteOrder.nativeOrder())

                // create a floating point buffer from the ByteBuffer
                asFloatBuffer().apply {
                    // add the coordinates to the FloatBuffer
                    put(MyGLRenderer().triangleCoords)
                    // set the buffer to read the first coordinate
                    position(0)
                }
            }


        private var positionHandle: Int = 0
        private var mColorHandle: Int = 0
        private val vertexCount: Int = MyGLRenderer().triangleCoords.size / COORDS_PER_VERTEX
        private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex


        init {
            val vertexShader: Int = MyGLRenderer().loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader: Int = MyGLRenderer().loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
            // create empty OpenGL ES Program
            mProgram = GLES20.glCreateProgram().also {

                // add the vertex shader to program
                GLES20.glAttachShader(it, vertexShader)

                // add the fragment shader to program
                GLES20.glAttachShader(it, fragmentShader)

                // creates OpenGL ES program executables
                GLES20.glLinkProgram(it)
            }
        }


        fun draw(mvpMatrix: FloatArray) {
            // Add program to OpenGL ES environment
            GLES20.glUseProgram(mProgram)

            // get handle to vertex shader's vPosition member
            positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {

                // Enable a handle to the triangle vertices
                GLES20.glEnableVertexAttribArray(it)

                // Prepare the triangle coordinate data
                GLES20.glVertexAttribPointer(
                    it,
                    COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT,
                    false,
                    vertexStride,
                    vertexBuffer
                )

                // get handle to fragment shader's vColor member
                mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                    // Set color for drawing the triangle
                    GLES20.glUniform4fv(colorHandle, 1, color, 0)
                }

                // get handle to shape's transformation matrix
                vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

                // Pass the projection and view transformation to the shader
                GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)


                // Draw the triangle
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

                // Disable vertex array
                GLES20.glDisableVertexAttribArray(it)
            }
        }
    }

    class Square {

        var squareCoords = floatArrayOf(
            -0.5f,  0.5f, 0.0f,      // top left
            -0.5f, -0.5f, 0.0f,      // bottom left
            0.5f, -0.5f, 0.0f,      // bottom right
            0.5f,  0.5f, 0.0f       // top right
        )
        private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

        // initialize vertex byte buffer for shape coordinates
        private val vertexBuffer: FloatBuffer =
            // (# of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(squareCoords.size * 4).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(squareCoords)
                    position(0)
                }
            }

        // initialize byte buffer for the draw list
        private val drawListBuffer: ShortBuffer =
            // (# of coordinate values * 2 bytes per short)
            ByteBuffer.allocateDirect(drawOrder.size * 2).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    put(drawOrder)
                    position(0)
                }
            }
    }

    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}