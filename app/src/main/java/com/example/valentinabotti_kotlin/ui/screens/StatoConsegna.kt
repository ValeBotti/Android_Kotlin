package com.example.valentinabotti_kotlin.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.plugin.locationcomponent.location
import com.example.valentinabotti_kotlin.R
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.valentinabotti_kotlin.data.local.PreferencesDataStore.getOid
import com.example.valentinabotti_kotlin.data.local.PreferencesDataStore.getSid
import com.example.valentinabotti_kotlin.model.Location
import com.example.valentinabotti_kotlin.model.MenuDitails
import com.example.valentinabotti_kotlin.model.Order_COMPLETED
import com.example.valentinabotti_kotlin.model.Order_ON_DELIVERY
import com.example.valentinabotti_kotlin.model.Screen
import com.example.valentinabotti_kotlin.ui.theme.DeepPurple
import com.example.valentinabotti_kotlin.ui.theme.PurpleGrey80
import com.example.valentinabotti_kotlin.viewmodel.LocationViewModel
import com.example.valentinabotti_kotlin.viewmodel.StatoConsegnaViewModel
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import kotlinx.coroutines.delay
import lilac
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.Int
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun StatoConsegna(
    sid: String,
    oid: Int,
    navController: NavHostController
) {

    val context = LocalContext.current

    val factoryLocation = viewModelFactory {
        initializer {
            LocationViewModel()
        }
    }

    val locationViewModel: LocationViewModel = viewModel(factory = factoryLocation)

    val factory = viewModelFactory {
        initializer {
            StatoConsegnaViewModel()
        }
    }

    val currentLocation by locationViewModel.currentLocation.collectAsState()

    val viewModel: StatoConsegnaViewModel = viewModel(factory = factory)

    var deliveryData_COMPLETED by remember {
        mutableStateOf(
            Order_COMPLETED(
                oid = 0,
                mid = 0,
                uid = 0,
                creationTimestamp = "",
                status = "NULL",
                deliveryLocation = Location(0f, 0f),
                currentPosition = Location(0f, 0f),
                deliveryTimestamp = ""
            )
        )
    }

    var deliveryData_ON_DELIVERY by remember {
        mutableStateOf(
            Order_ON_DELIVERY(
                oid = 0,
                mid = 0,
                uid = 0,
                creationTimestamp = "",
                status = "NULL",
                deliveryLocation = Location(0f, 0f),
                expectedDeliveryTimestamp = "",
                currentPosition = Location(0f, 0f)
            )
        )
    }

    var menu by remember { mutableStateOf(
        MenuDitails(
            mid = 0,
            name = "Nome menu",
            price = 0.0,
            location = mapOf("lat" to 0f, "lng" to 0f),
            imageVersion = 0,
            shortDescription = "Descrizione menu",
            deliveryTime = 0,
            longDescription = "Descrizione lunga menu"
        )
    ) }

    var sidState by remember { mutableStateOf(sid) }

    LaunchedEffect(Unit) {
        Log.d("StatoConsegna", "LaunchedEffect(Unit) → START retrieveLocation()")
        locationViewModel.retrieveLocation(context)
    }

    LaunchedEffect(currentLocation) {

        Log.d("StatoConsegna", "Polling started → currentLocation = ${currentLocation.lat}, ${currentLocation.lng}")

        if (currentLocation.lat == 0f || currentLocation.lng == 0f) {
            Log.d("StatoConsegna", "Location INVALID → polling aborted")
            return@LaunchedEffect
        }

        val oidCurrent = getOid(context) ?: oid
        val sidCurrent = getSid(context) ?: sid

        while (true) {

            Log.d("StatoConsegna", "Polling → fetchOrder(oid=$oidCurrent, sid=$sidCurrent)")

            val orderFetched = viewModel.fetchOrder(oidCurrent, sidCurrent)

            when (orderFetched) {

                is Order_ON_DELIVERY -> {
                    deliveryData_ON_DELIVERY = orderFetched
                    Log.d("StatoConsegna", "Order_ON_DELIVERY updated → $deliveryData_ON_DELIVERY")
                    menu = viewModel.retriveMenuDitails(deliveryData_ON_DELIVERY.mid, sidState, currentLocation.lat, currentLocation.lng)
                }

                is Order_COMPLETED -> {
                    deliveryData_COMPLETED = orderFetched
                    Log.d("StatoConsegna", "Order_COMPLETED updated → $deliveryData_COMPLETED")
                    menu = viewModel.retriveMenuDitails(deliveryData_COMPLETED.mid, sidState, currentLocation.lat, currentLocation.lng)

                    break
                }

                else -> {
                    Log.e("StatoConsegna", "Order fetch error → $orderFetched")
                }
            }

            val midToFetch =
                if (deliveryData_ON_DELIVERY.mid != 0) deliveryData_ON_DELIVERY.mid
                else deliveryData_COMPLETED.mid

            if (midToFetch != 0) {
                Log.d("StatoConsegna", "Fetching menu → mid=$midToFetch")

                menu = viewModel.retriveMenuDitails(
                    midToFetch,
                    sidCurrent,
                    currentLocation.lat,
                    currentLocation.lng
                )

                Log.d("StatoConsegna", "Menu updated → $menu")
            } else {
                Log.e("StatoConsegna", "NO VALID MID FOUND → menu not fetched")
            }

            delay(3000.milliseconds)
        }

        Log.d("StatoConsegna", "Polling terminated")
    }

    if (currentLocation != Location(0f, 0f)) {

        when {
            deliveryData_ON_DELIVERY.status.equals("ON_DELIVERY") && menu.mid != 0 -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.systemBars.asPaddingValues())
                ) {

                    val mapViewportState = rememberMapViewportState()

                    LaunchedEffect(deliveryData_ON_DELIVERY.currentPosition) {
                        mapViewportState.setCameraOptions {
                            center(
                                Point.fromLngLat(
                                    deliveryData_ON_DELIVERY.currentPosition.lng.toDouble(),
                                    deliveryData_ON_DELIVERY.currentPosition.lat.toDouble()
                                )
                            )
                            zoom(14.5)
                        }
                    }

                    MapboxMap(
                        modifier = Modifier
                            .fillMaxSize(),
                        mapViewportState = mapViewportState
                    ) {
                        MapEffect(Unit) { mapView ->
                            mapView.location.updateSettings {
                                enabled = false
                            }
                        }

                        val delivery_position = rememberIconImage(
                            key = R.drawable.position_marker,
                            painter = painterResource(R.drawable.position_marker),
                        )

                        val drone = rememberIconImage(
                            key = R.drawable.drone,
                            painter = painterResource(R.drawable.drone),
                        )
                        Log.d(
                            "MarkerPosition",
                            "Lat: ${deliveryData_ON_DELIVERY.currentPosition.lat}, Lng: ${deliveryData_ON_DELIVERY.currentPosition.lng}"
                        )
                        Log.d("MarkerIcon", "Icon Loaded: ${R.drawable.position_marker}")
                        PointAnnotation(
                            point = Point.fromLngLat(
                                deliveryData_ON_DELIVERY.currentPosition.lng.toDouble(),
                                deliveryData_ON_DELIVERY.currentPosition.lat.toDouble()
                            )
                        ) {
                            iconImage = drone
                        }

                        PointAnnotation(
                            point = Point.fromLngLat(
                                deliveryData_ON_DELIVERY.deliveryLocation.lng.toDouble(),
                                deliveryData_ON_DELIVERY.deliveryLocation.lat.toDouble()
                            )
                        ) {
                            iconImage = delivery_position
                        }
                    }

                    Column(
                        modifier = Modifier
                            .background(Color.Transparent)
                    ) {

                        Column(
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(5.dp)
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "Informazioni sull'ultimo ordine effettuato",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(
                                        DeepPurple,
                                        shape = RoundedCornerShape(5.dp)
                                    ),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Nome menu: " + menu.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Prezzo menu: " + menu.price + " €",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Data ed ora acquisto: " + ZonedDateTime.parse(
                                    deliveryData_ON_DELIVERY.creationTimestamp
                                )
                                    .withZoneSameInstant(ZoneId.of("Europe/Rome"))
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Stato consegna: In consegna",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                textAlign = TextAlign.Center
                            )
                        }
                        IconButton(
                            onClick = { navController.navigate(Screen.HomeListaMenu.route) },
                            modifier = Modifier
                                .padding(10.dp)
                                .size(35.dp)
                                .background(
                                    Color.White.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(5.dp)
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.left_arrow),
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }

            deliveryData_COMPLETED.status.equals("COMPLETED") && menu.mid != 0 -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.systemBars.asPaddingValues())
                ) {

                    val mapViewportState = rememberMapViewportState {
                        setCameraOptions {
                            center(
                                Point.fromLngLat(
                                    deliveryData_COMPLETED.currentPosition.lng.toDouble(),
                                    deliveryData_COMPLETED.currentPosition.lat.toDouble()
                                )
                            )
                            zoom(14.0)
                        }

                    }

                    MapboxMap(
                        mapViewportState = mapViewportState
                    ) {
                        MapEffect(Unit) { mapView ->
                            mapView.location.updateSettings {
                                enabled = false
                            }
                        }

                        val drone = rememberIconImage(
                            key = R.drawable.drone,
                            painter = painterResource(R.drawable.drone),
                        )

                        Log.d(
                            "MarkerPosition",
                            "Lat: ${deliveryData_COMPLETED.currentPosition.lat}, Lng: ${deliveryData_COMPLETED.currentPosition.lng}"
                        )
                        Log.d("MarkerIcon", "Icon Loaded: ${R.drawable.drone}")
                        PointAnnotation(
                            point = Point.fromLngLat(
                                deliveryData_COMPLETED.deliveryLocation.lng.toDouble(),
                                deliveryData_COMPLETED.deliveryLocation.lat.toDouble()
                            )
                        ) {
                            iconImage = drone
                        }
                    }

                    Column(
                        modifier = Modifier
                            .background(Color.Transparent)
                    ) {

                        Column(
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(5.dp)
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "Informazioni sull'ultimo ordine effettuato",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(
                                        DeepPurple,
                                        shape = RoundedCornerShape(5.dp)
                                    ),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Nome menu: " + menu.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Prezzo menu: " + menu.price + " €",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Data ed ora acquisto: " + ZonedDateTime.parse(
                                    deliveryData_COMPLETED.creationTimestamp
                                )
                                    .withZoneSameInstant(ZoneId.of("Europe/Rome"))
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Stato consegna: completato",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                textAlign = TextAlign.Center
                            )
                        }
                        IconButton(
                            onClick = { navController.navigate(Screen.HomeListaMenu.route) },
                            modifier = Modifier
                                .padding(10.dp)
                                .size(35.dp)
                                .background(
                                    Color.White.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(5.dp)
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.left_arrow),
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }

            menu.mid == 0 && deliveryData_ON_DELIVERY.status.equals("NULL") && deliveryData_COMPLETED.status.equals("NULL") -> {

                Box() {

                    val mapViewportState = rememberMapViewportState {
                        setCameraOptions {
                            center(
                                Point.fromLngLat(
                                    currentLocation.lng.toDouble(),
                                    currentLocation.lat.toDouble()
                                )
                            )
                            zoom(18.0)
                        }
                    }

                    MapboxMap(
                        modifier = Modifier
                            .fillMaxSize(),
                        mapViewportState = mapViewportState,
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

                    IconButton(
                        onClick = { navController.navigate(Screen.HomeListaMenu.route) },
                        modifier = Modifier
                            .padding(10.dp)
                            .size(35.dp)
                            .background(
                                Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(5.dp)
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.left_arrow),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }

            }

            else -> Spinner()
        }

    } else {
        Spinner()
    }
}