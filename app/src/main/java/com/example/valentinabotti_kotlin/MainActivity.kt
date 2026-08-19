package com.example.valentinabotti_kotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import com.example.valentinabotti_kotlin.ui.theme.ValentinaBotti_KotlinTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.valentinabotti_kotlin.model.Screen
import com.example.valentinabotti_kotlin.viewmodel.MainActivityViewModel
import com.example.valentinabotti_kotlin.viewmodel.retrieveCurrentPage
import com.example.valentinabotti_kotlin.viewmodel.saveCurrentPage
class MainActivity : ComponentActivity() {

    private val mainActivityViewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Lifecycle", "onCreate called")
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ValentinaBotti_KotlinTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    App(
                        modifier = Modifier.padding(innerPadding)
                    )

                    StatusBarProtection()

                }
            }
        }
    }
}


@Composable
fun App(
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val mainActivityViewModel: MainActivityViewModel = viewModel()
    mainActivityViewModel.retriveSidUid(context = context)

    val sidUid = mainActivityViewModel.sidUid.observeAsState()

    val navController = rememberNavController()

    Navigation(
        navController = navController,
        sid = sidUid.value?.sid ?: "",
        uid = sidUid.value?.uid ?: 0
    )

    val lifecycle = ProcessLifecycleOwner.get().lifecycle

    DisposableEffect(navController, lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    Log.d("NavigationObserver", "onStart")

                    retrieveCurrentPage(context) { screenRoute ->
                        if (!screenRoute.isNullOrEmpty() && screenRoute != Screen.Splash.route) {
                            Log.d("NavigationObserver", "Navigated to: $screenRoute")
                            navController.navigate(screenRoute)
                        }
                    }
                }

                Lifecycle.Event.ON_STOP -> {
                    Log.d("NavigationObserver", "onStop")

                    val currentRoute = navController.currentBackStackEntry?.destination?.route
                    if (!currentRoute.isNullOrEmpty() && currentRoute != Screen.Splash.route) {
                        saveCurrentPage(context, currentRoute)
                        Log.d("NavigationObserver", "Saved page: $currentRoute")
                    }
                }

                else -> {}
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun StatusBarProtection(
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                with(LocalDensity.current) {
                    (WindowInsets.statusBars.getTop(this) * 1.2f).toDp()
                }
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 1f),
                        color.copy(alpha = 0.8f),
                        Color.Transparent
                    )
                )
            )
    )
}