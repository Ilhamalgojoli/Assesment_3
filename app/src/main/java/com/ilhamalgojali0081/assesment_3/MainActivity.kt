package com.ilhamalgojali0081.assesment_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ilhamalgojali0081.assesment_3.ui.theme.Assesment_3Theme
import com.ilhamalgojali0081.assesment_3.ui.theme.screen.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assesment_3Theme {
                MainScreen()
            }
        }
    }
}
