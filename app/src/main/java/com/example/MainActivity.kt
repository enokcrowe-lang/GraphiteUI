package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.home.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: LauncherViewModel = viewModel()
            val preferences by viewModel.preferences.collectAsState()

            MyApplicationTheme(
                darkTheme = true,
                isTrueAmoled = preferences.isTrueAmoledBlack
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh battery and app list when returning to Home screen
    }
}
