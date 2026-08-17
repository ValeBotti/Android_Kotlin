package com.example.valentinabotti_kotlin.viewmodel

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.valentinabotti_kotlin.data.remote.ApiCalls
import com.example.valentinabotti_kotlin.model.Location
import com.example.valentinabotti_kotlin.model.MenuDitails
import com.example.valentinabotti_kotlin.model.Order_COMPLETED
import com.example.valentinabotti_kotlin.model.Order_ON_DELIVERY
import com.example.valentinabotti_kotlin.model.ProfileData
import com.example.valentinabotti_kotlin.model.ProfileDataString
import com.example.valentinabotti_kotlin.model.Screen
import com.example.valentinabotti_kotlin.model.UidSid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfiloUtenteViewModel() : ViewModel() {

    suspend fun retriveUser(sid: String?, uid: Int?): ProfileDataString {
        return withContext(Dispatchers.IO) {
            try {
                if (sid != null && uid != null) {
                    val userData = ApiCalls.getRetriveUser(sid, uid)
                    Log.d("RecuperoUtente", "User retrieved: ${!userData.firstName.isNullOrBlank()}")
                    if (!userData.firstName.isNullOrBlank()) {
                        Log.d("RecuperoUtente", "User retrieved: $userData")
                        userData
                        ProfileDataString(
                            firstName = userData.firstName,
                            lastName = userData.lastName,
                            cardFullName = userData.cardFullName,
                            cardNumber = userData.cardNumber,
                            cardExpireMonth = "0" + userData.cardExpireMonth.toString(),
                            cardExpireYear = userData.cardExpireYear.toString(),
                            cardCVV = userData.cardCVV,
                            uid = userData.uid,
                            lastOid = userData.lastOid,
                            orderStatus = userData.orderStatus
                        )
                    } else {
                        Log.d("RecuperoUtente", "User retrieved, but null: $userData")
                        ProfileDataString(
                            firstName = "Nome",
                            lastName = "Cognome",
                            cardFullName = "Nome Cognome",
                            cardNumber = "0000000000000000",
                            cardExpireMonth = "00",
                            cardExpireYear = "0000",
                            cardCVV = "000",
                            uid = uid,
                            lastOid = 0,
                            orderStatus = "NONE"
                        )
                    }
                } else {
                    Log.d("RecuperoUtente", "SID or UID is null")
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
                    ) // Ritorna null se sid o uid sono null
                }
            } catch (e: Exception) {
                Log.e("RecuperoUtente", "Error during user retrieval: ${e.message}")
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
                ) // Ritorna null in caso di errore
            }
        }
    }

    fun sendUserInfoToServer(datiProfiloString: ProfileDataString, context: Context, sid: String?, uid: Int?, navController: NavHostController) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Validazione dei dati con messaggi utente
                if (!validateProfileData(datiProfiloString, context)) {
                    Log.d("sendUserInfoToServer", "Dati non validi.")
                    return@launch
                }

                if (sid != null && uid != null && datiProfiloString.cardExpireMonth.isDigitsOnly() && datiProfiloString.cardExpireYear.isDigitsOnly()) {
                    val datiProfilo = ProfileData(
                        firstName = datiProfiloString.firstName,
                        lastName = datiProfiloString.lastName,
                        cardFullName = datiProfiloString.cardFullName,
                        cardNumber = datiProfiloString.cardNumber,
                        cardExpireMonth = datiProfiloString.cardExpireMonth.toInt(),
                        cardExpireYear = datiProfiloString.cardExpireYear.toInt(),
                        cardCVV = datiProfiloString.cardCVV,
                        uid = datiProfiloString.uid,
                        lastOid = datiProfiloString.lastOid,
                        orderStatus = datiProfiloString.orderStatus
                    )
                    ApiCalls.putUserNewInfo(sid, uid, datiProfilo)
                    Log.d("sendUserInfoToServer", "Dati inviati correttamente.")
                    AlertDialog.Builder(context)
                        .setTitle("Dati personali salvati")
                        .setMessage("Dati inviati correttamente.")
                        .setPositiveButton("Torna alla home") { _, _ ->
                            navController.navigate(Screen.HomeListaMenu.route)
                        }
                        .setNegativeButton("Ignora", null)//in questo modo il dialog si chiude automaticamente
                        .show()
                } else {
                    Toast.makeText(context, "SID o UID non validi.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Errore durante l'invio dei dati: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    suspend fun fetchOrder_ON_DELIVERY(oid: Int?, sid: String?): Order_ON_DELIVERY {
        return withContext(Dispatchers.IO) {
            try {
                if (sid != null && oid != null) {
                    val order = ApiCalls.getOrder(oid = oid, sid) as Order_ON_DELIVERY
                    Log.d("RecuperoOrdine", "Order retrieved: $order")
                    order
                } else {
                    Log.d("RecuperoOrdine", "SID is null")
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
                }
            } catch (e: Exception) {
                Log.e("RecuperoOrdine", "Error during order retrieval: ${e.message}")
                Order_ON_DELIVERY(
                    oid = 0,
                    mid = 0,
                    uid = 0,
                    creationTimestamp = "",
                    status = "ERROR",
                    deliveryLocation = Location(0f, 0f),
                    expectedDeliveryTimestamp = "",
                    currentPosition = Location(0f, 0f)
                )
            }
        }
    }

    suspend fun fetchOrder_COMPLETED(oid: Int?, sid: String?): Order_COMPLETED {
        return withContext(Dispatchers.IO) {
            try {
                if (sid != null && oid != null) {
                val order = ApiCalls.getOrder(oid = oid, sid) as Order_COMPLETED
                Log.d("RecuperoOrdine", "Order retrieved: $order")
                order
                } else {
                    Log.d("RecuperoOrdine", "SID is null")
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
                }
            } catch (e: Exception) {
                Log.e("RecuperoOrdine", "Error during order retrieval: ${e.message}")
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
            }
        }
    }


    suspend fun retriveMenuDitails(mid: Int?, sid: String?, lat: Float, lng: Float): MenuDitails {
        return withContext(Dispatchers.IO) {
            try {
                if (sid != null && mid != null) {
                    val dettagliMenu = ApiCalls.getMenuDitails(mid = mid)
                    Log.d("DettagliMenuViewModel", "Image retrieved: $dettagliMenu")
                    dettagliMenu
                } else {
                    Log.d("DettagliMenuViewModel", "SID or UID is null")
                    MenuDitails(
                        mid = 0,
                        name = "Nome menu",
                        price = 0.0,
                        location = mapOf("lat" to lat, "lng" to lng),
                        imageVersion = 0,
                        shortDescription = "Descrizione menu",
                        deliveryTime = 0,
                        longDescription = "Descrizione lunga menu"
                    )
                }
            } catch (e: Exception) {
                Log.e("DettagliMenuViewModel", "Error during image retrieval: ${e.message}")
                MenuDitails(
                    mid = 0,
                    name = "Nome menu",
                    price = 0.0,
                    location = mapOf("lat" to lat, "lng" to lng),
                    imageVersion = 0,
                    shortDescription = "Descrizione menu",
                    deliveryTime = 0,
                    longDescription = "Descrizione lunga menu"
                )
            } as MenuDitails
        }
    }
}

fun validateProfileData(datiProfiloString: ProfileDataString, context: Context): Boolean {
    return when {
        // Verifica il nome
        datiProfiloString.firstName.isNullOrBlank() -> {
            Toast.makeText(context, "Il nome è obbligatorio.", Toast.LENGTH_SHORT).show()
            false
        }
        // Verifica il cognome
        datiProfiloString.lastName.isNullOrBlank() -> {
            Toast.makeText(context, "Il cognome è obbligatorio.", Toast.LENGTH_SHORT).show()
            false
        }
        // Verifica il nome completo sulla carta
        datiProfiloString.cardFullName.isNullOrBlank() -> {
            Toast.makeText(context, "Il nome completo sulla carta è obbligatorio.", Toast.LENGTH_SHORT).show()
            false
        }
        // Verifica il numero della carta (16 cifre)
        datiProfiloString.cardNumber.isNullOrBlank() || datiProfiloString.cardNumber.length != 16 -> {
            Toast.makeText(context, "Numero di carta non valido: deve avere 16 cifre.", Toast.LENGTH_SHORT).show()
            false
        }
        // Verifica il mese di scadenza (tra 1 e 12)
        datiProfiloString.cardExpireMonth.isNullOrBlank() || datiProfiloString.cardExpireMonth.toInt() !in 1..12 -> {
            Toast.makeText(context, "Il mese di scadenza deve essere tra 01 e 12.", Toast.LENGTH_SHORT).show()
            false
        }
        // Verifica l'anno di scadenza (almeno 2025)
        datiProfiloString.cardExpireYear.toString().isNullOrBlank()  -> {
            Toast.makeText(context, "L'anno di scadenza deve essere maggiore di 2025.", Toast.LENGTH_SHORT).show()
            false
        }
        // Verifica il CVV (3 cifre minimo)
        datiProfiloString.cardCVV.isNullOrBlank() || datiProfiloString.cardCVV.toString().length != 3 -> {
            Toast.makeText(context, "Il CVV deve avere 3 cifre.", Toast.LENGTH_SHORT).show()
            false
        }
        else -> true // Tutti i controlli superati
    }
}