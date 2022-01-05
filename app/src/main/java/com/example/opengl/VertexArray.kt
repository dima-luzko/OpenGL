package com.example.opengl

import android.opengl.GLES20
import android.opengl.GLES20.glVertexAttribPointer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VertexArray {
    private var buffer: FloatBuffer? = null

    fun vertexArray(vertexData: FloatArray){
        buffer = ByteBuffer
            .allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)
    }

    fun getVertexAttribute(attributeLocation: Int,componentCount: Int, texture: Int, textureLocation: Int){
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
}