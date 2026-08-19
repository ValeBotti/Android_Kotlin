package com.example.valentinabotti_kotlin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.valentinabotti_kotlin.data.remote.ApiCalls
import com.example.valentinabotti_kotlin.model.ImageUI
import com.example.valentinabotti_kotlin.model.Menu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeListaMenuViewModel() : ViewModel() {

    suspend fun retriveMenuList(lat: Float, lng: Float, sid: String?): List<Menu> {
        return withContext(Dispatchers.IO) {
            try {
                if (sid != null) {
                    val menuList = ApiCalls.getMenuList(lat, lng, sid)
                    Log.d("RecuperoMenu", "Menu retrieved: ${menuList.size}")
                    menuList
                } else {
                    Log.d("RecuperoMenu", "SID or UID is null")
                    listOf(
                        Menu(
                            mid = 0,
                            name = "Nome",
                            price = 0.0,
                            location = mapOf("lat" to 0.0, "lng" to 0.0),
                            imageVersion = 0,
                            shortDescription = "Descrizione",
                            deliveryTime = 0
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("RecuperoMenu", "Error during menu retrieval: ${e.message}")
                listOf(
                    Menu(
                        mid = 0,
                        name = "Nome",
                        price = 0.0,
                        location = mapOf("lat" to 0.0, "lng" to 0.0),
                        imageVersion = 0,
                        shortDescription = "Descrizione",
                        deliveryTime = 0
                    )
                )// Ritorna null in caso di errore
            }
        }
    }

    suspend fun retrieveImageMenu(mid: Int, sid: String?) : ImageUI {
        return withContext(Dispatchers.IO) {
            try {
                if (sid != null) {
                    val image = ApiCalls.getImageMenu(mid = mid, sid = sid)
                    Log.d("RecuperoImmagine", "Image retrieved: $image")

                    image
                } else {
                    Log.d("RecuperoImmagine", "SID or UID is null")
                    "null"
                }
            } catch (e: Exception) {
                Log.e("RecuperoImmagine", "Error during image retrieval: ${e.message}")
                "null"// Ritorna null in caso di errore
            } as ImageUI
        }
    }
}