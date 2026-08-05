import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.valentinabotti_kotlin.data.local.DBController
import com.example.valentinabotti_kotlin.data.local.Image
import com.example.valentinabotti_kotlin.data.local.PreferencesDataStore.getMid
import com.example.valentinabotti_kotlin.data.local.PreferencesDataStore.getSid
import com.example.valentinabotti_kotlin.data.local.PreferencesDataStore.saveMid
import com.example.valentinabotti_kotlin.model.ImageUI
import com.example.valentinabotti_kotlin.model.MenuDitails
import com.example.valentinabotti_kotlin.model.Screen
import com.example.valentinabotti_kotlin.ui.components.Base64Image
import com.example.valentinabotti_kotlin.ui.components.CustomButton
import com.example.valentinabotti_kotlin.ui.screens.Spinner
import com.example.valentinabotti_kotlin.ui.theme.DeepPurple
import com.example.valentinabotti_kotlin.ui.theme.DeeperPurple
import com.example.valentinabotti_kotlin.ui.theme.Purple80
import com.example.valentinabotti_kotlin.ui.theme.PurpleGrey80
import com.example.valentinabotti_kotlin.viewmodel.DettagliMenuViewModel
import com.example.valentinabotti_kotlin.viewmodel.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DettagliMenu(
    sid: String,
    uid: Int,
    mid: Int,
    navController: NavHostController,
) {

    var buy by remember { mutableStateOf(false) }

    Log.d("DettagliMenu", "MID: $mid SID: $sid UID: $uid")

    var context = LocalContext.current

    val factoryLocation = viewModelFactory {
        initializer {
            LocationViewModel()
        }
    }

    val locationViewModel: LocationViewModel = viewModel(factory = factoryLocation)


    val factory = viewModelFactory {
        initializer {
            DettagliMenuViewModel()
        }
    }

    val viewModelDettagli: DettagliMenuViewModel = viewModel(factory = factory)

    var image by remember { mutableStateOf(ImageUI("")) }
    var menuId by remember { mutableStateOf(mid) }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("MyComposable", "App in background. Salvataggio: $mid")
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch {
                        if (mid != 0) {
                            saveMid(context, mid)
                        }
                    }
                }

                Lifecycle.Event.ON_RESUME -> {
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch {
                        if (mid == 0) {
                            menuId = getMid(context)?:0
                        } else {
                            menuId = mid
                        }
                    }
                    Log.d("MyComposable", "App in foreground. Caricato: $menuId")
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var menu by remember { mutableStateOf(MenuDitails(
        mid = 0,
        name = "Nome menu",
        price = 0.0,
        location = mapOf("lat" to 0f, "lng" to 0f),
        imageVersion = 0,
        shortDescription = "Descrizione menu",
        deliveryTime = 0,
        longDescription = "Descrizione lunga menu"
    )) }

    LaunchedEffect(mid, menuId) {

        try {
            val db = DBController.getDatabase(context)
            val base64 = db.imageDao().getBase64ByMid(menuId)
            if (base64 != null) {
                image = ImageUI(base64)
                Log.d("MenuCard", "Image retrieved from DB: ${image.base64}")
            } else {
                image = viewModelDettagli.retrieveImageMenu(menuId, sid)
                if (!image.equals("")) {
                    db.imageDao().insertImage(Image(menuId, image.base64, menu.imageVersion))
                    Log.d("MenuCard", "Image retrieved from server: ${image.base64}")
                } else {
                    Log.d("MenuCard", "Image not found on server")
                }
            }
        } catch (e: Exception) {
            Log.e("MenuCard", "Error retrieving image from DB: ${e.message}")
        }
    }

    locationViewModel.retrieveLocation(context)
    val currentLocation by locationViewModel.currentLocation.collectAsState()

    val hasPermission by locationViewModel.hasPermission.observeAsState()

    LaunchedEffect(currentLocation) {

        if (menuId != 0) {
            Log.d("DettagliMenu", "Menu ID: $menuId")
            try {
                val sid = getSid(context)?:""
                menu = viewModelDettagli.retriveMenuDitails(menuId, sid, currentLocation.lat, currentLocation.lng)
                Log.d("DettagliMenu", "Menu retrieved ui1: ${menu.name}")
            } catch (e: Exception) {
                Log.e("DettagliMenu", "Error saving MID: ${e.message}")
            }
        } else {
            try {
                val sid = getSid(context)?:""
                menuId = viewModelDettagli.fetchMid(context)?:0
                menu = viewModelDettagli.retriveMenuDitails(menuId, sid, currentLocation.lat, currentLocation.lng)
                Log.d("DettagliMenu", "sid: $sid")
                Log.d("DettagliMenu", "Menu retrieved ui2: ${menu.name}")
                Log.d("DettagliMenu", "Location: $currentLocation")
                Log.d("DettagliMenu", "Menu ID no mid: $menuId")
            } catch (e: Exception) {
                Log.e("DettagliMenu", "Error retrieving MID: ${e.message}")
            }
        }
    }

    LaunchedEffect(buy) {

        try {
            if (buy) {
                if (hasPermission == false) {
                    Toast.makeText(context, "Non puoi acquistare senza aver dato i permessi di localizzazione", Toast.LENGTH_SHORT).show()
                } else {
                    var canPurchase = viewModelDettagli.purchaseMenuHandler(menuId, sid, uid, context, currentLocation.lat, currentLocation.lng)

                    if (canPurchase != 0 && hasPermission == true) {
                        Log.d("DettagliMenu", "oid: ${canPurchase}")
                        navController.navigate(route = "statoConsegna/${sid}/${canPurchase}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("DettagliMenu", "Error checking purchase: ${e.message}")
        }
    }

    if (menu == MenuDitails(
            mid = 0,
            name = "Nome menu",
            price = 0.0,
            location = mapOf("lat" to 0f, "lng" to 0f),
            imageVersion = 0,
            shortDescription = "Descrizione menu",
            deliveryTime = 0,
            longDescription = "Descrizione lunga menu"
        )) {
        Spinner()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    WindowInsets.systemBars.asPaddingValues()
                )
                .padding(horizontal = 16.dp), // Aggiunge padding orizzontale
        ) {
            item() {
                Button(
                    onClick = { navController.navigate(Screen.HomeListaMenu.route) }, // Usa il callback per cambiare pagina
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Usa l'icona di una freccia a sinistra
                        contentDescription = "Home lista menu"
                    )
                    Text(text = "Back")
                }
            }
            item() {
                Text(
                    text = menu.name,
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    color = DeepPurple,
                    fontWeight = FontWeight.Bold
                )
            }
            item() {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(350.dp) // altezza per l'immagine
                        .background(Color.Gray) // placeholder dell'immagine
                ) {
                    Log.d("DettagliMenu", "Image: ${image.base64}")
                    if (image.base64.isNotEmpty() && !image.base64.equals("Immagine")) {
                        Base64Image(image.base64)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
            item() {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = DeeperPurple, shape = RoundedCornerShape(25))
                        .padding(horizontal = 4.dp),
                ) {
                    Text(
                        text = "Prezzo: ${String.format(menu.price.toString()).replace('.', ',')}€",
                        color = Color.White,
                        modifier = Modifier
                            .padding(end = 100.dp),
                        fontSize = 12.sp // Imposta una dimensione del testo più piccola
                    )

                    Text(
                        text = "Tempo di consegna: ${viewModelDettagli.formatDeliveryTime(menu.deliveryTime)}",
                        color = Color.White,
                        fontSize = 12.sp // Imposta una dimensione del testo più piccola
                    )
                }
            }
            item() {
                Text(
                    text = menu.shortDescription,
                    modifier = Modifier.padding(5.dp)
                        .background(PurpleGrey80),
                    color = Color.Black,
                    fontStyle = FontStyle.Italic
                )
            }
            item() {
                Text(
                    text = menu.longDescription,
                    modifier = Modifier.padding(5.dp),
                    color = Color.Black
                )
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(), // Assicura che il Box occupi tutta la larghezza disponibile
                    contentAlignment = Alignment.CenterEnd // Allinea il contenuto a destra
                ) {
                    CustomButton(
                        text = "Acquista",
                        onClick = { buy = true },
                        modifier = Modifier
                    )
                }
            }
        }
    }
}