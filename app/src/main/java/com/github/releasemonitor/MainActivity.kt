package com.github.releasemonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.github.releasemonitor.data.StorageManager
import com.github.releasemonitor.ui.MainScreen
import com.github.releasemonitor.ui.miuix.MiuixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val storage = StorageManager(this)

        setContent {
            MiuixTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(storage = storage)
                }
            }
        }
    }
}
