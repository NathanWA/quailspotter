package com.nathan.quailspotter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        try {
            val clazz = Class.forName("org.kmp.playground.kflite.AppContext")
            val instance = clazz.getDeclaredField("INSTANCE").apply { isAccessible = true }.get(null)
            clazz.getDeclaredMethod("setUp", android.content.Context::class.java).apply { isAccessible = true }.invoke(instance, applicationContext)
        } catch (e: Exception) {
            android.util.Log.e("QuailSpotter", "Failed to initialize kflite AppContext", e)
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}