package com.alestreaks.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alestreaks.app.theme.AleStreaksTheme
import com.alestreaks.app.ui.AppScreen
import com.alestreaks.app.ui.MainViewModel
import com.alestreaks.app.util.ServiceLocator
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val viewModel = ViewModelProvider(this, MainViewModelFactory())[MainViewModel::class.java]

        setContent {
            var darkTheme by remember { mutableStateOf(false) }

            AleStreaksTheme(darkTheme = darkTheme) {
                AppScreen(
                    viewModel = viewModel,
                    darkTheme = darkTheme,
                    onDarkThemeChange = { darkTheme = it },
                )
            }
        }
    }
}

private class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(
            authRepository = ServiceLocator.authRepository,
            taskRepository = ServiceLocator.taskRepository,
        ) as T
    }
}
