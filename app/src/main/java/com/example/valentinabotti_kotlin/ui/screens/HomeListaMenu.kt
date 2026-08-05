
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.valentinabotti_kotlin.model.Location
import com.example.valentinabotti_kotlin.model.Menu
import com.example.valentinabotti_kotlin.model.Screen
import com.example.valentinabotti_kotlin.ui.components.CustomButton
import com.example.valentinabotti_kotlin.ui.components.MenuCard
import com.example.valentinabotti_kotlin.ui.screens.Spinner
import com.example.valentinabotti_kotlin.viewmodel.HomeListaMenuViewModel
import com.example.valentinabotti_kotlin.viewmodel.LocationViewModel
import com.example.valentinabotti_kotlin.viewmodel.MainActivityViewModel


@Composable
fun HomeListaMenu(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    sid: String,
    uid: Int
) {
    val context = LocalContext.current

    //creo il viewmodel dello screen
    val factoryHomeListaMenu = viewModelFactory {
        initializer {
            HomeListaMenuViewModel()
        }
    }

    val viewModelHomeListaMenu: HomeListaMenuViewModel = viewModel(factory = factoryHomeListaMenu)


    var menuList by remember { mutableStateOf<List<Menu>>(emptyList()) }


    val locationViewModel : LocationViewModel = viewModel()
    val hasPermission by locationViewModel.hasPermission.observeAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> locationViewModel.onPermissionResult(isGranted, context) }

    val currentLocation by locationViewModel.currentLocation.collectAsState()

    LaunchedEffect(hasPermission) {
        if(hasPermission == false || hasPermission == null) {
            locationViewModel.requestPermission(context, permissionLauncher)
        } else {
            locationViewModel.retrieveLocation(context)
        }
    }

    Log.d("HomeListaMenu", "Current location out: $currentLocation")

    LaunchedEffect(currentLocation) {
        if (currentLocation != Location(0.0f, 0.0f)) {
            menuList = viewModelHomeListaMenu.retriveMenuList(
                currentLocation.lat,
                currentLocation.lng,
                sid
            )
            Log.d("HomeListaMenu", "Menu lista ${menuList}")
        }
    }

    if(menuList.size > 0) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()), // Rispetta i margini del notch
        ) {
            if (hasPermission == false) {
                item {
                    Text(
                        text = "PERMESSI DI POSIZIONE NEGATI!",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(Color.White)
                            .padding(8.dp),
                        color = Color(0xFF4B0082)
                    )
                }

            }
            item {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp), // Padding a destra per spostare le icone
                ) {
                    IconButton(
                        onClick = { navController.navigate(Screen.ProfiloUtente.route) },
                        modifier = Modifier
                            .size(50.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.user_circle),
                            contentDescription = "Profilo utente",
                            tint = Color.Unspecified // Mantieni i colori originali dell'icona
                        )
                    }

                    CustomButton(
                        text = "Mappa",
                        icon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null) },
                        onClick = { navController.navigate(route = "statoConsegna/${sid}/${0}") },
                        modifier = Modifier
                            .padding(start= 150.dp)
                    )
                }
            }

            items(menuList.size) { index ->
                MenuCard(menuList[index], sid, uid, navController, viewModelHomeListaMenu)
            }
        }
    } else {
        Spinner()
    }
}