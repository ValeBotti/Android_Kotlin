import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.valentinabotti_kotlin.R
import com.example.valentinabotti_kotlin.model.Location
import com.example.valentinabotti_kotlin.model.Order_COMPLETED
import com.example.valentinabotti_kotlin.model.Order_ON_DELIVERY
import com.example.valentinabotti_kotlin.model.MenuDitails
import com.example.valentinabotti_kotlin.model.ProfileDataString
import com.example.valentinabotti_kotlin.model.Screen
import com.example.valentinabotti_kotlin.ui.components.CustomButton
import com.example.valentinabotti_kotlin.ui.theme.DeepPurple
import com.example.valentinabotti_kotlin.ui.theme.DeeperPurple
import com.example.valentinabotti_kotlin.ui.theme.PurpleGrey80
import com.example.valentinabotti_kotlin.viewmodel.ProfiloUtenteViewModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val lilac = Color(0xFFC8A2C8)
val darkLilac = Color(0xFFAB8AB3)
val green = Color(0xFFB8D89A)
val deepLilac = Color(0xFF8E7099)

@Composable
fun ProfiloUtente(
    navController: NavHostController,
    sid: String,
    uid: Int
) {
    val context = LocalContext.current

    val factoryProfiloUtente = viewModelFactory {
        initializer {
            ProfiloUtenteViewModel()
        }
    }

    val viewModelProfiloUtente: ProfiloUtenteViewModel = viewModel(factory = factoryProfiloUtente)


    var datiProfilo by remember {
        mutableStateOf(
            ProfileDataString(
                firstName = "Nome",
                lastName = "Cognome",
                cardFullName = "Nome Cognome",
                cardNumber = "0000000000000000",
                cardExpireMonth = "00",
                cardExpireYear = "0000",
                cardCVV = "000",
                uid = 0,
                lastOid = 0,
                orderStatus = "NONE"
            )
        )
    }


    var datiUltimoOrdine_ON_DELIVERY by remember {
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

    var datiUltimoOrdine_COMPLETED by remember {
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

    var menu by remember {
        mutableStateOf(
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
        )
    }

    var profileDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(sid, uid) {

        datiProfilo = viewModelProfiloUtente.retriveUser(sid, uid)
        Log.d("ProfiloUtente", "dati profilo ${datiProfilo}")

        profileDataLoaded = true

        if (datiProfilo.orderStatus == "ON_DELIVERY") {
            datiUltimoOrdine_ON_DELIVERY =
                viewModelProfiloUtente.fetchOrder_ON_DELIVERY(datiProfilo.lastOid, sid)
            if (datiUltimoOrdine_ON_DELIVERY.mid != 0) {
                menu = viewModelProfiloUtente.retriveMenuDitails(
                    datiUltimoOrdine_ON_DELIVERY.mid,
                    sid,
                    datiUltimoOrdine_ON_DELIVERY.currentPosition.lat,
                    lng = datiUltimoOrdine_ON_DELIVERY.currentPosition.lng
                )
            }
            Log.d("ProfiloUtente", "Ultimo ordine in consegna menu: $menu")
        } else if (datiProfilo.orderStatus == "COMPLETED") {
            datiUltimoOrdine_COMPLETED =
                viewModelProfiloUtente.fetchOrder_COMPLETED(datiProfilo.lastOid, sid)
            if (datiUltimoOrdine_COMPLETED.mid != 0) {
                menu = viewModelProfiloUtente.retriveMenuDitails(
                    datiUltimoOrdine_COMPLETED.mid,
                    sid,
                    datiUltimoOrdine_COMPLETED.currentPosition.lat,
                    lng = datiUltimoOrdine_COMPLETED.currentPosition.lng
                )
            }
            Log.d("ProfiloUtente", "Ultimo ordine consegnato menu: $menu")
        }
    }

    if (profileDataLoaded) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(WindowInsets.systemBars.asPaddingValues()),
        ) {
            if (menu.mid != 0 && (datiUltimoOrdine_COMPLETED.status != "NULL" || datiUltimoOrdine_ON_DELIVERY.status != "NULL")) {
                item {

                    var dataTimePurchase = ""
                    var deliveryStatus = ""
                    if (datiUltimoOrdine_ON_DELIVERY.status == "ON_DELIVERY") {
                        dataTimePurchase = datiUltimoOrdine_ON_DELIVERY.creationTimestamp
                        deliveryStatus = "In consegna"
                    } else if (datiUltimoOrdine_COMPLETED.status == "COMPLETED") {
                        dataTimePurchase = datiUltimoOrdine_COMPLETED.creationTimestamp
                        deliveryStatus = "Conseganto"
                    }

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
                                    PurpleGrey80,
                                    shape = RoundedCornerShape(5.dp)
                                ),

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
                                dataTimePurchase
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
                            text = "Stato consegna: ${deliveryStatus}",
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
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .background(
                            DeepPurple,
                            shape = RoundedCornerShape(5.dp)
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigate(Screen.HomeListaMenu.route) },
                        modifier = Modifier
                            .padding(10.dp)
                            .size(35.dp)
                            .background(
                                Color.White.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(5.dp)
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.left_arrow),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        CustomButton(
                            text = "Save",
                            onClick = { viewModelProfiloUtente.sendUserInfoToServer(datiProfilo, context, sid, uid, navController)}
                        )
                    }
                }

            }

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = datiProfilo.firstName.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = PurpleGrey80,
                            unfocusedTextColor = Color.Black,
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.White,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black,
                        ),
                        singleLine = true,
                        placeholder = { Text("Nome") },
                        onValueChange = { datiProfilo = datiProfilo.copy(firstName = it) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = { Text("Nome") }
                    )

                    TextField(
                        value = datiProfilo.lastName.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = PurpleGrey80,
                            unfocusedTextColor = Color.Black,
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.White,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black
                        ),
                        singleLine = true,
                        placeholder = { Text("Cognome") },
                        onValueChange = { datiProfilo = datiProfilo.copy(lastName = it) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = { Text("Cognome") }
                    )

                    TextField(
                        value = datiProfilo.cardFullName.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = PurpleGrey80,
                            unfocusedTextColor = Color.Black,
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.White,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black
                        ),
                        singleLine = true,
                        placeholder = { Text("Nome Cognome") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardFullName = it) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = { Text("Nome Cognome") }
                    )

                    TextField(
                        value = datiProfilo.cardNumber.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = PurpleGrey80,
                            unfocusedTextColor = Color.Black,
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.White,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black
                        ),
                        singleLine = true,
                        placeholder = { Text("Numero carta") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardNumber = it) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = { Text("Numero carta") }
                    )

                    TextField(
                        value = datiProfilo.cardExpireMonth,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = PurpleGrey80,
                            unfocusedTextColor = Color.Black,
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.White,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black
                        ),
                        singleLine = true,
                        placeholder = { Text("Mese scadenza carta") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardExpireMonth = it) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = { Text("Mese scadenza carta") }
                    )

                    TextField(
                        value = datiProfilo.cardExpireYear,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = PurpleGrey80,
                            unfocusedTextColor = Color.Black,
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.White,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black
                        ),
                        singleLine = true,
                        placeholder = { Text("Anno scadenza carta") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardExpireYear = it) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = { Text("Anno scadenza carta") }
                    )

                    TextField(
                        value = datiProfilo.cardCVV.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            unfocusedTextColor = Color.Black,
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.White,
                            cursorColor = Color.Black,
                            focusedTextColor = PurpleGrey80,
                            focusedLabelColor = Color.Black
                        ),
                        singleLine = true,
                        placeholder = { Text("CVV") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardCVV = it) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = { Text("CVV") }
                    )
                }
            }
        }
    }
}