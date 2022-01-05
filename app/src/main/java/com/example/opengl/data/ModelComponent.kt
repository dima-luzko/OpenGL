package com.example.opengl.data


class ModelComponent {

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

    fun createTexture(first: Int, second: Int, third: Int, four: Int, five: Int, six: Int) : IntArray {
        return intArrayOf(first,second,third,four,five,six)
    }


}