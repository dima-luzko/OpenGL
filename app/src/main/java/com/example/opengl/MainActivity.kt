package com.example.opengl

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mSurface: MainSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isSupportedES2(this)) {
            Toast.makeText(this, "OpenGl ES 2.0 is not supported", Toast.LENGTH_LONG).show()
            finish()
        }
        mSurface = this.findViewById(R.id.surface) as MainSurfaceView
    }

    private fun isSupportedES2(context: Context): Boolean {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = manager.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x20000
    }

    override fun onPause() {
        super.onPause()
        mSurface.onPause()
    }

    override fun onResume() {
        super.onResume()
        mSurface.onResume()
    }
}
