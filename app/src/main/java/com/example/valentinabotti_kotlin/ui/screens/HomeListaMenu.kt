package com.example.valentinabotti_kotlin.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.valentinabotti_kotlin.R
import com.example.valentinabotti_kotlin.model.Menu
import com.example.valentinabotti_kotlin.model.Screen
import com.example.valentinabotti_kotlin.ui.components.MenuCard
import com.example.valentinabotti_kotlin.viewmodel.HomeListaMenuViewModel
import com.example.valentinabotti_kotlin.viewmodel.LocationViewModel
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.example.valentinabotti_kotlin.ui.theme.Purple40
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.locationcomponent.location

@Composable
fun HomeListaMenu(
    navController: NavHostController,
    sid: String
) {
    val context = LocalContext.current

    val factoryHomeListaMenu = viewModelFactory {
        initializer {
            HomeListaMenuViewModel()
        }
    }

    val viewModelHomeListaMenu: HomeListaMenuViewModel = viewModel(factory = factoryHomeListaMenu)

    var menuList by remember { mutableStateOf<List<Menu>>(emptyList()) }

    val locationViewModel: LocationViewModel = viewModel()

    val hasPermission by locationViewModel.hasPermission.observeAsState()
    val currentLocation by locationViewModel.currentLocation.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationViewModel.onPermissionResult(isGranted)
    }

    LaunchedEffect(hasPermission) {
        when (hasPermission) {
            null -> locationViewModel.requestPermission(permissionLauncher)
            true -> locationViewModel.retrieveLocation(context)
            false -> Log.d("HomeListaMenu", "Permission denied")
        }
    }

    LaunchedEffect(currentLocation) {
        if (currentLocation.lat != 0f && currentLocation.lng != 0f) {
            menuList = viewModelHomeListaMenu.retriveMenuList(
                currentLocation.lat,
                currentLocation.lng,
                sid
            )
            Log.d("HomeListaMenu", "Menu lista $menuList")
        }
    }

    val activity = LocalActivity.current ?: return

    SideEffect {
        val window = activity.window

        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.navigationBarColor = Color.Black.toArgb()

        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightNavigationBars = false

        window.statusBarColor = Color.Black.toArgb()

        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
    }

    if (menuList.size > 0) {

        Scaffold(
            modifier = Modifier.fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .background(Color.Black),
            topBar = {
                Box(
                    modifier = Modifier
                        .height(65.dp)
                        .fillMaxWidth()
                        .background(Color.Black)
                ) {

                    if (currentLocation.lat != 0f && currentLocation.lng != 0f) {

                        val mapViewportState = rememberMapViewportState {
                            setCameraOptions {
                                center(
                                    Point.fromLngLat(
                                        currentLocation.lng.toDouble(),
                                        currentLocation.lat.toDouble()
                                    )
                                )
                                zoom(12.0)
                            }
                        }

                        MapboxMap(
                            modifier = Modifier
                                .height(60.dp)
                                .align(Alignment.TopEnd)
                                .width(300.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            mapViewportState = mapViewportState
                        ) {

                            MapEffect(Unit) { mapView ->
                                mapView.location.updateSettings {
                                    enabled = true
                                }
                            }

                            PointAnnotation(
                                point = Point.fromLngLat(
                                    currentLocation.lng.toDouble(),
                                    currentLocation.lat.toDouble()
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.TopEnd)
                            .width(300.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .clickable {
                                navController.navigate("statoConsegna/${sid}/${0}")
                            }
                    )

                    IconButton(
                        onClick = { navController.navigate(Screen.ProfiloUtente.route) },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding( 6.dp)
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Profilo utente",
                            tint = Color.White
                        )
                    }
                }
            }
        ) { innerPadding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding()
                    )
                    .background(Color.Black),
            ) {

                if (hasPermission == false) {
                    item {
                        Text(
                            text = "PERMESSI DI POSIZIONE NEGATI!",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .background(Color.White),
                            color = Color(0xFF4B0082)
                        )
                    }
                }

                items(menuList.size) { index ->
                    MenuCard(menuList[index], sid, navController, viewModelHomeListaMenu)
                }
            }
        }
    } else {
        Spinner()
    }
}