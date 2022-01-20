package com.example.opengl.data

import androidx.renderscript.Float4


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

    val verticesSecondModel = verticesFirstModel.map {
        if (it == -1f) {
            it - 0.1f
        } else {
            it + 0.1f
        }
    }.toFloatArray()


    private val a = Float4(-1f, 1f, 1f, 1f)
    private val b = Float4(1f, 1f, 1f, 1f)
    private val c = Float4(-1f, -1f, 1f, 1f)
    private val d = Float4(1f, -1f, 1f, 1f)
    private val e = Float4(-1f, 1f, -1f, 1f)
    private val f = Float4(1f, 1f, -1f, 1f)
    private val g = Float4(-1f, -1f, -1f, 1f)
    private val h = Float4(1f, -1f, -1f, 1f)
    val coordForFirstModel: Array<Vertex> = arrayOf(
        Vertex(pos = a),
        Vertex(pos = b),
        Vertex(pos = c),
        Vertex(pos = d),
        Vertex(pos = e),
        Vertex(pos = f),
        Vertex(pos = g),
        Vertex(pos = h)
    )


    private val a2 = Float4(-1.1f, 1.1f, 1.1f, 1.1f)
    private val b2 = Float4(1.1f, 1.1f, 1.1f, 1.1f)
    private val c2 = Float4(-1.1f, -1.1f, 1.1f, 1.1f)
    private val d2 = Float4(1.1f, -1.1f, 1.1f, 1.1f)
    private val e2 = Float4(-1.1f, 1.1f, -1.1f, 1.1f)
    private val f2 = Float4(1.1f, 1.1f, -1.1f, 1.1f)
    private val g2 = Float4(-1.1f, -1.1f, -1.1f, 1.1f)
    private val h2 = Float4(1.1f, -1.1f, -1.1f, 1.1f)
    val coordForSecondModel: Array<Vertex> = arrayOf(
        Vertex(pos = a2),
        Vertex(pos = b2),
        Vertex(pos = c2),
        Vertex(pos = d2),
        Vertex(pos = e2),
        Vertex(pos = f2),
        Vertex(pos = g2),
        Vertex(pos = h2)
    )

    val mappedCoordsForFirstModel = mutableListOf<Float>().apply {
        coordForFirstModel.forEach {
            add(it.pos.x)
            add(it.pos.y)
            add(it.pos.z)
        }
    }

    val mappedCoordsForSecondModel = mutableListOf<Float>().apply {
        coordForSecondModel.forEach {
            add(it.pos.x)
            add(it.pos.y)
            add(it.pos.z)
        }
    }

    fun createTexture(
        first: Int,
        second: Int,
        third: Int,
        four: Int,
        five: Int,
        six: Int
    ): IntArray {
        return intArrayOf(first, second, third, four, five, six)
    }


}