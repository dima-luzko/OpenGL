package com.example.opengl.data

import androidx.renderscript.Float4
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


object ModelComponent {

    data class Vertex(val pos: Float4)

    val verticesFirstModel = floatArrayOf(
        -1f, 1f, 1f,
        1f, 1f, 1f,
        -1f, -1f, 1f,
        1f, -1f, 1f,
        -1f, 1f, -1f,
        1f, 1f, -1f,
        -1f, -1f, -1f,
        1f, -1f, -1f,
    )

    val a = Float4(-1f,1f,1f,1f)
    val b = Float4( 1f, 1f, 1f,1f)
    val c = Float4(-1f, -1f, 1f,1f)
    val d = Float4( 1f, -1f, 1f,1f)
    val e = Float4( -1f, 1f, -1f,1f)
    val f = Float4(1f, 1f, -1f,1f)
    val g = Float4( -1f, -1f, -1f,1f)
    val h = Float4(1f, -1f, -1f,1f)
    val coord : Array<Vertex> = arrayOf(
        Vertex(pos = a),
        Vertex(pos = b),
        Vertex(pos = c),
        Vertex(pos = d),
        Vertex(pos = e),
        Vertex(pos = b),
        Vertex(pos = f),
        Vertex(pos = g),
        Vertex(pos = h)
    )

    private var buffer: FloatBuffer? = null

    fun vertexArray() {
        buffer = ByteBuffer
            .allocateDirect(coord.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(coord)

    }



    val verticesSecondModel = verticesFirstModel.map {
        if (it == -1f) {
            it - 0.1f
        } else {
            it + 0.1f
        }
    }.toFloatArray()

    fun createTexture(first: Int, second: Int, third: Int, four: Int, five: Int, six: Int) : IntArray {
        return intArrayOf(first,second,third,four,five,six)
    }


}