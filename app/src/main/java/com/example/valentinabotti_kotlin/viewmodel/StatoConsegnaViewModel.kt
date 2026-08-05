package com.example.valentinabotti_kotlin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.valentinabotti_kotlin.data.remote.ApiCalls
import com.example.valentinabotti_kotlin.model.Location
import com.example.valentinabotti_kotlin.model.MenuDitails
import com.example.valentinabotti_kotlin.model.Order
import com.example.valentinabotti_kotlin.model.Order_COMPLETED
import com.example.valentinabotti_kotlin.model.Order_ON_DELIVERY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatoConsegnaViewModel() : ViewModel() {

    suspend fun fetchOrder(oid: Int, sid: String): Order {
        return withContext(Dispatchers.IO) {
            try {
                val order = ApiCalls.getOrder(oid = oid, sid = sid)
                Log.d("RecuperoOrdine", "Order retrieved: $order")
                order
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

    suspend fun retriveMenuDitails(mid: Int?, sid: String, lat: Float, lng: Float): MenuDitails {
        return withContext(Dispatchers.IO) {
            try {
                if (sid != null && mid != null) {
                    val dettagliMenu = ApiCalls.getMenuDitails(mid = mid, sid = sid, lat = lat, lng = lng)
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