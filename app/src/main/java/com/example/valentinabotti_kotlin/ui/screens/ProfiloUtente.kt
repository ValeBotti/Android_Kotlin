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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.valentinabotti_kotlin.model.Location
import com.example.valentinabotti_kotlin.model.Order_COMPLETED
import com.example.valentinabotti_kotlin.model.Order_ON_DELIVERY
import com.example.valentinabotti_kotlin.model.MenuDitails
import com.example.valentinabotti_kotlin.model.ProfileDataString
import com.example.valentinabotti_kotlin.model.Screen
import com.example.valentinabotti_kotlin.ui.screens.Spinner
import com.example.valentinabotti_kotlin.viewmodel.ProfiloUtenteViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val lilac = Color(0xFFC8A2C8)
val darkLilac = Color(0xFFAB8AB3)
val green = Color(0xFFB8D89A)
val deepLilac = Color(0xFF8E7099)

@Composable
fun ProfiloUtente(
    modifier: Modifier = Modifier,
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


    var datiProfilo by remember { mutableStateOf(ProfileDataString(
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
    )) }



    var datiUltimoOrdine_ON_DELIVERY by remember { mutableStateOf(
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
    ) }

    var datiUltimoOrdine_COMPLETED by remember { mutableStateOf(
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
    ) }

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

    LaunchedEffect(Unit) {

        datiProfilo = viewModelProfiloUtente.retriveUser(sid, uid)
        Log.d("ProfiloUtente", "dati profilo ${datiProfilo}")

        if (datiProfilo.orderStatus == "ON_DELIVERY") {
            datiUltimoOrdine_ON_DELIVERY = viewModelProfiloUtente.fetchOrder_ON_DELIVERY(datiProfilo.lastOid, sid)
            if (datiUltimoOrdine_ON_DELIVERY.mid != 0) {
                menu = viewModelProfiloUtente.retriveMenuDitails(datiUltimoOrdine_ON_DELIVERY.mid, sid, datiUltimoOrdine_ON_DELIVERY.currentPosition.lat, lng = datiUltimoOrdine_ON_DELIVERY.currentPosition.lng)
            }
            Log.d("ProfiloUtente", "Ultimo ordine in consegna menu: $menu")
        } else if (datiProfilo.orderStatus == "COMPLETED") {
            datiUltimoOrdine_COMPLETED = viewModelProfiloUtente.fetchOrder_COMPLETED(datiProfilo.lastOid, sid)
            if (datiUltimoOrdine_COMPLETED.mid != 0) {
                menu = viewModelProfiloUtente.retriveMenuDitails(datiUltimoOrdine_COMPLETED.mid, sid, datiUltimoOrdine_COMPLETED.currentPosition.lat, lng = datiUltimoOrdine_COMPLETED.currentPosition.lng)
            }
            Log.d("ProfiloUtente", "Ultimo ordine consegnato menu: $menu")
        }
    }

    if ((menu.mid != 0 && datiUltimoOrdine_COMPLETED.status != "NULL") || (menu.mid != 0 && datiUltimoOrdine_ON_DELIVERY.status != "NULL")) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(WindowInsets.systemBars.asPaddingValues()), // Rispetta i margini del notch
        ) {
            item {
                Text(
                    text = "Profilo Utente",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Column {
                    when (datiUltimoOrdine_COMPLETED.status) {
                        "COMPLETED" -> {
                            Text(
                                text = "Informazioni sull'ultimo ordine effettuato",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(lilac, shape = RoundedCornerShape(16.dp)),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Nome menu: " + menu.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .border(2.dp, lilac, shape = RoundedCornerShape(8.dp)),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Prezzo menu: " + menu.price + " €",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .border(2.dp, lilac, shape = RoundedCornerShape(15.dp)),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Data ed ora acquisto: " + ZonedDateTime.parse(datiUltimoOrdine_COMPLETED.creationTimestamp)
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .border(2.dp, lilac, shape = RoundedCornerShape(8.dp)),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Stato consegna: completato",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp) // Margine esterno
                                    .border(2.dp, lilac, shape = RoundedCornerShape(8.dp)),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    when (datiUltimoOrdine_ON_DELIVERY.status) {
                        "ON_DELIVERY" -> {
                            Text(
                                text = "Informazioni sull'ultimo ordine effettuato",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .background(lilac, shape = RoundedCornerShape(16.dp)),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Nome menu: " + menu.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .border(2.dp, lilac, shape = RoundedCornerShape(8.dp)),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Prezzo menu: " + menu.price + " €",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .border(2.dp, lilac, shape = RoundedCornerShape(15.dp)),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Data ed ora acquisto: " + ZonedDateTime.parse(datiUltimoOrdine_ON_DELIVERY.creationTimestamp)
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .border(2.dp, lilac, shape = RoundedCornerShape(8.dp)),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Stato consegna: in consegna",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .border(2.dp, lilac, shape = RoundedCornerShape(8.dp)),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            item {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .border(width = 2.dp, color = green, shape = RoundedCornerShape(0.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pulsante a sinistra con icona per tornare a homeListaMenu
                    Button(
                        onClick = { navController.navigate(Screen.HomeListaMenu.route) }, // Usa il callback per cambiare pagina
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Usa l'icona di una freccia a sinistra
                            contentDescription = "Torna alla Home"
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f)) // Spazio per separare i pulsanti

                    // Pulsante a destra con scritta "Salva"
                    Button(
                        onClick = { viewModelProfiloUtente.sendUserInfoToServer(datiProfilo, context, sid, uid, navController) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.White
                        )
                    ) {
                        Text("Salva")
                    }
                }
            }

            // Aggiungi una lista di elementi per simulare contenuto aggiuntivo scrollabile
            item {
                Column {
                    TextField(
                        value = datiProfilo.firstName.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black
                        ),
                        singleLine = true,
                        placeholder = { Text("Nome") },
                        onValueChange = { datiProfilo = datiProfilo.copy(firstName = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("Nome") }
                    )

                    TextField(
                        value = datiProfilo.lastName.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black // Colore del cursore
                        ),
                        singleLine = true,
                        placeholder = { Text("Cognome") },
                        onValueChange = { datiProfilo = datiProfilo.copy(lastName = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("Cognome") }
                    )

                    TextField(
                        value = datiProfilo.cardFullName.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black // Colore del cursore
                        ),
                        singleLine = true,
                        placeholder = { Text("Nome Cognome") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardFullName = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("Nome Cognome") }
                    )

                    TextField(
                        value = datiProfilo.cardNumber.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black // Colore del cursore
                        ),
                        singleLine = true,
                        placeholder = { Text("Numero carta") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardNumber = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("Numero carta") }
                    )

                    TextField(
                        value = datiProfilo.cardExpireMonth.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black // Colore del cursore
                        ),
                        singleLine = true,
                        placeholder = { Text("Mese scadenza carta") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardExpireMonth = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("Mese scadenza carta") }
                    )

                    TextField(
                        value = datiProfilo.cardExpireYear.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black // Colore del cursore
                        ),
                        singleLine = true,
                        placeholder = { Text("Anno scadenza carta") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardExpireYear = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("Anno scadenza carta") }
                    )

                    TextField(
                        value = datiProfilo.cardCVV.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black // Colore del cursore
                        ),
                        singleLine = true,
                        placeholder = { Text("CVV") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardCVV = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("CVV") }
                    )
                }
            }
        }
    } else if ((datiProfilo.orderStatus == "NONE" || datiProfilo.orderStatus == null) && datiProfilo.uid != 0) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(WindowInsets.systemBars.asPaddingValues()), // Rispetta i margini del notch
        ) {
            item {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .border(width = 2.dp, color = green, shape = RoundedCornerShape(0.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pulsante a sinistra con icona per tornare a homeListaMenu
                    Button(
                        onClick = { navController.navigate(Screen.HomeListaMenu.route) }, // Usa il callback per cambiare pagina
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Usa l'icona di una freccia a sinistra
                            contentDescription = "Torna alla Home"
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f)) // Spazio per separare i pulsanti

                    // Pulsante a destra con scritta "Salva"
                    Button(
                        onClick = { viewModelProfiloUtente.sendUserInfoToServer(datiProfilo, context, sid, uid, navController) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.White
                        )
                    ) {
                        Text("Salva")
                    }
                }
            }


            // Aggiungi una lista di elementi per simulare contenuto aggiuntivo scrollabile
            item {
                Column {
                    TextField(
                        value = datiProfilo.firstName.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.White,
                            cursorColor = Color.Black
                        ),
                        singleLine = true,
                        placeholder = { Text("Nome") },
                        onValueChange = { datiProfilo = datiProfilo.copy(firstName = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("Nome") }
                    )

                    TextField(
                        value = datiProfilo.lastName.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black // Colore del cursore
                        ),
                        singleLine = true,
                        placeholder = { Text("Cognome") },
                        onValueChange = { datiProfilo = datiProfilo.copy(lastName = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("Cognome") }
                    )

                    TextField(
                        value = datiProfilo.cardFullName.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black // Colore del cursore
                        ),
                        singleLine = true,
                        placeholder = { Text("Nome Cognome") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardFullName = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("Nome Cognome") }
                    )

                    TextField(
                        value = datiProfilo.cardNumber.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black // Colore del cursore
                        ),
                        singleLine = true,
                        placeholder = { Text("Numero carta") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardNumber = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("Numero carta") }
                    )

                    TextField(
                        value = datiProfilo.cardExpireMonth.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black // Colore del cursore
                        ),
                        singleLine = true,
                        placeholder = { Text("Mese scadenza carta") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardExpireMonth = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("Mese scadenza carta") }
                    )

                    TextField(
                        value = datiProfilo.cardExpireYear.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black // Colore del cursore
                        ),
                        singleLine = true,
                        placeholder = { Text("Anno scadenza carta") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardExpireYear = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("Anno scadenza carta") }
                    )

                    TextField(
                        value = datiProfilo.cardCVV.toString(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Sfondo quando il campo è attivo
                            unfocusedContainerColor = Color.White, // Sfondo quando il campo non è attivo
                            focusedTextColor = Color.Black, // Colore del testo quando il campo è attivo
                            unfocusedTextColor = Color.Black, // Colore del testo quando il campo è inattivo
                            focusedIndicatorColor = Color.Gray, // Colore del bordo quando è attivo
                            unfocusedIndicatorColor = Color.White, // Colore del bordo quando è inattivo
                            cursorColor = Color.Black // Colore del cursore
                        ),
                        singleLine = true,
                        placeholder = { Text("CVV") },
                        onValueChange = { datiProfilo = datiProfilo.copy(cardCVV = it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .background(Color.White),
                        label = { Text("CVV") }
                    )
                }
            }
        }
    } else {
        Spinner()
    }
}