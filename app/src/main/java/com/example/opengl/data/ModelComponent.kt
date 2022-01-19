package com.example.opengl.data

import android.util.Log
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
        Vertex(pos = f),
        Vertex(pos = g),
        Vertex(pos = h)
    )

    val a2 = Float4(-1.1f,1.1f,1.1f,1.1f)
    val b2 = Float4( 1.1f, 1.1f, 1.1f,1.1f)
    val c2 = Float4(-1.1f, -1.1f, 1.1f,1.1f)
    val d2 = Float4( 1.1f, -1.1f, 1.1f,1.1f)
    val e2 = Float4( -1.1f, 1.1f, -1.1f,1.1f)
    val f2 = Float4(1.1f, 1.1f, -1.1f,1.1f)
    val g2 = Float4( -1.1f, -1.1f, -1.1f,1.1f)
    val h2 = Float4(1.1f, -1.1f, -1.1f,1.1f)
    val coord2 : Array<Vertex> = arrayOf(
        Vertex(pos = a2),
        Vertex(pos = b2),
        Vertex(pos = c2),
        Vertex(pos = d2),
        Vertex(pos = e2),
        Vertex(pos = f2),
        Vertex(pos = g2),
        Vertex(pos = h2)
    )

    val mappedCoords = mutableListOf<Float>().apply {
        coord.forEach {
            add(it.pos.x)
            add(it.pos.y)
            add(it.pos.z)
            add(it.pos.w)
        }
    }

    val mappedCoords2 = mutableListOf<Float>().apply {
        coord2.forEach {
            add(it.pos.x)
            add(it.pos.y)
            add(it.pos.z)
            add(it.pos.w)
        }
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