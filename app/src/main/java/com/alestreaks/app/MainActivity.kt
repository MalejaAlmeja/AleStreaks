package com.alestreaks.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
            AleStreaksTheme {
                AppScreen(viewModel = viewModel)
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
