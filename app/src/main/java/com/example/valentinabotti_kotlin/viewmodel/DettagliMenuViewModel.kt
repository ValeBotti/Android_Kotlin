package com.example.valentinabotti_kotlin.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.valentinabotti_kotlin.data.local.PreferencesDataStore.getMid
import com.example.valentinabotti_kotlin.data.remote.ApiCalls
import com.example.valentinabotti_kotlin.model.ImageUI
import com.example.valentinabotti_kotlin.model.MenuDitails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DettagliMenuViewModel() : ViewModel() {

    suspend fun retriveMenuDitails(mid: Int?, sid: String, lat: Float, lng: Float): MenuDitails {
        return withContext(Dispatchers.IO) {
            try {
                if (mid != null) {
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
            }
        }
    }

    suspend fun retrieveImageMenu(mid: Int?, sid: String): ImageUI {
        return withContext(Dispatchers.IO) {
            try {
                if (mid != null) {
                    val image = ApiCalls.getImageMenu(mid = mid, sid = sid)
                    Log.d("DettagliMenuViewModel", "Image retrieved: $image")
                    image
                } else {
                    Log.d("DettagliMenuViewModel", "SID or UID is null")
                    ""
                }
            } catch (e: Exception) {
                Log.e("DettagliMenuViewModel", "Error during image retrieval: ${e.message}")
                "" // Return empty string in case of error
            } as ImageUI
        }
    }

    suspend fun fetchMid(context: Context): Int? {
        return withContext(Dispatchers.IO) {
            try {
                val mid = getMid(context)
                mid
            } catch (e: Exception) {
                Log.e("DettagliMenuViewModel", "Error retrieving MID: ${e.message}")
                null
            }
        }
    }

    suspend fun purchaseMenuHandler(mid: Int, sid: String, uid: Int, context: Context, lat: Float, lng: Float): Int {
        return withContext(Dispatchers.IO) {
            try {
                // Recupera i dati utente
                val userData = ApiCalls.getRetriveUser(sid, uid)

                Log.d("DettagliMenuViewModel", "${userData}")
                if (userData.firstName == "Nome" || userData.firstName == null) {
                    Log.d("DettagliMenuViewModel", "User data not compiled")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Compila i dati utente", Toast.LENGTH_SHORT).show()
                    }
                    return@withContext 0 // Dati utente non compilati, acquisto non possibile
                }

                // Effettua l'acquisto del menu
                try {
                    val orderData = ApiCalls.postMenu(mid, sid, userData.cardNumber, lat, lng)
                    when (orderData.status) {
                        "INVALID_CARD" -> {
                            Log.d("DettagliMenuViewModel", "Invalid card")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Il numero della tua carta di credito non è valido, vai al tuo profilo e cambialo!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return@withContext 0
                        }
                        "ORDER_ALREADY_ON_DELIVERY" -> {
                            Log.d("DettagliMenuViewModel", "Order already on delivery")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Hai già effettuato un ordine!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return@withContext 0
                        }
                        "ERROR" -> {
                            Log.d("DettagliMenuViewModel", "Server error")
                            return@withContext 0
                        }
                        "NULL" -> {
                            Log.d("DettagliMenuViewModel", "Unknown error")
                            return@withContext orderData.oid
                        }
                        else -> {
                            Log.d("DettagliMenuViewModel", "Menu purchased successfully")
                            return@withContext orderData.oid
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DettagliMenuViewModel", "Error in postMenu: ${e.message}")
                    return@withContext 0
                }
            } catch (e: Exception) {
                Log.e("DettagliMenuViewModel", "Error retrieving user data: ${e.message}")
                return@withContext 0 // Restituisce 0 in caso di eccezione
            }
        }
    }

    fun formatDeliveryTime(deliveryTime: Int): String {
        Log.d("DettagliMenuViewModel", "Delivery time: $deliveryTime")
        val hours = deliveryTime / 60
        val minutes = deliveryTime % 60
        return "$hours h $minutes min"
    }
}

