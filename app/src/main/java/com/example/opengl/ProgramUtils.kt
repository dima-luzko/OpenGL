package com.example.opengl

import android.content.Context
import android.opengl.GLES20.*


class ProgramUtils {
    fun createProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programId = glCreateProgram()
        if (programId == 0) {
            return 0
        }
        glAttachShader(programId, vertexShaderId)
        glAttachShader(programId, fragmentShaderId)
        glLinkProgram(programId)
        val linkStatus = IntArray(1)
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            glDeleteProgram(programId)
            return 0
        }
        return programId
    }

    fun createShader(context: Context?, type: Int, shaderRawId: Int): Int {
        val shaderText = context?.let { FileGLRawReader().readTextFromRaw(it,shaderRawId) }
        return ProgramUtils().createShader(type,shaderText)
    }

    fun createShader(type: Int, shaderText: String?): Int {
        val shaderId = glCreateShader(type)
        if (shaderId == 0) {
            return 0
        }
        glShaderSource(shaderId, shaderText)
        glCompileShader(shaderId)
        val compileStatus = IntArray(1)
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderId)
            return 0
        }
        return shaderId
    }
}