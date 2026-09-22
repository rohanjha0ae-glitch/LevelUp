package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.CompositionLocalProvider
import com.example.ui.LocalScrollableFlingBehavior
import com.example.ui.AuthViewModel
import com.example.ui.LevelUpLoadingScreen
import com.example.ui.LevelUpMainScreen
import com.example.ui.LevelUpViewModel
import com.example.ui.LoginScreen
import com.example.ui.rememberSmoothFlingBehavior
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LevelUpViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val isAppInitializing by viewModel.isAppInitializing.collectAsStateWithLifecycle()
            val authViewModel: AuthViewModel = viewModel()
            val userName by authViewModel.userName.collectAsStateWithLifecycle()
            
            MyApplicationTheme(darkTheme = isDarkTheme) {
                CompositionLocalProvider(LocalScrollableFlingBehavior provides rememberSmoothFlingBehavior()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (isAppInitializing) {
                            LevelUpLoadingScreen()
                        } else if (userName == null) {
                            LoginScreen(authViewModel = authViewModel)
                        } else {
                            LevelUpMainScreen(viewModel = viewModel, authViewModel = authViewModel)
                        }
                    }
                }
            }
        }
    }
}

