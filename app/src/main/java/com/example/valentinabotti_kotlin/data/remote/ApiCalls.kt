package com.example.valentinabotti_kotlin.data.remote

import java.lang.Exception
import android.util.Log
import com.example.valentinabotti_kotlin.model.BodyOrder
import com.example.valentinabotti_kotlin.model.ImageUI
import com.example.valentinabotti_kotlin.model.Location
import com.example.valentinabotti_kotlin.model.Menu
import com.example.valentinabotti_kotlin.model.MenuDitails
import com.example.valentinabotti_kotlin.model.Order
import com.example.valentinabotti_kotlin.model.Order_COMPLETED
import com.example.valentinabotti_kotlin.model.Order_NULL
import com.example.valentinabotti_kotlin.model.Order_ON_DELIVERY
import com.example.valentinabotti_kotlin.model.ProfileData
import com.example.valentinabotti_kotlin.model.ProfileDataModifiedByUser
import com.example.valentinabotti_kotlin.model.UidSid
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.collections.mapOf


object ApiCalls {

    private val TAG = ApiCalls::class.simpleName

    // Funzione per creare un nuovo utente
    suspend fun postCreateUser(): UidSid {
        Log.d(TAG, "postCreateUser")

        val url = "${CommunicationController.BASE_URL}/session/create"

        return try {
            val httpResponse =
                CommunicationController.genericRequest(url, CommunicationController.HttpMethod.POST)

            // Controllo se la risposta è andata a buon fine
            if (httpResponse.status.value == 200) {
                val result: UidSid = httpResponse.body()

                // Accedi a sid e uid dal corpo della risposta
                Log.d(TAG, "sid: ${result.sid}, uid: ${result.uid}")

                return result
            } else {
                // Se lo status non è 200, ritorniamo un valore di fallback
                Log.e(TAG, "Request failed. HTTP Status: ${httpResponse.status.value}")
                UidSid("", -1) // Risultato di fallback
            }
        } catch (e: Exception) {
            Log.e(TAG, "Request error: ${e.message}")
            UidSid("", -1) // Risultato di fallback in caso di eccezione
        }
    }

    suspend fun getRetriveUser(sid: String, uid: Int): ProfileData {
        Log.d(TAG, "getRetriveUser")

        val url = "${CommunicationController.BASE_URL}/user/$uid"

        return try {
            val httpResponse = CommunicationController.genericRequest(
                url,
                CommunicationController.HttpMethod.GET,
                queryParameters = mapOf("sid" to sid)
            )

            // Controllo se la risposta è andata a buon fine
            if (httpResponse.status.value == 200) {
                val result: ProfileData = httpResponse.body()

                // Accedi a sid e uid dal corpo della risposta
                Log.d(TAG, "dati utente: ${result}")

                return result
            } else {
                // Se lo status non è 200, ritorniamo un valore di fallback
                Log.e(TAG, "Request failed. HTTP Status: ${httpResponse.status.value}")
                ProfileData(
                    firstName = "Nome",
                    lastName = "Cognome",
                    cardFullName = "Nome Cognome",
                    cardNumber = "0000000000000000",
                    cardExpireMonth = 0,
                    cardExpireYear = 0,
                    cardCVV = "000",
                    uid = 0,
                    lastOid = 0,
                    orderStatus = "NONE"
                ) // Risultato di fallback
            }
        } catch (e: Exception) {
            Log.e(TAG, "Request error: ${e.message}")
            ProfileData(
                firstName = "Nome",
                lastName = "Cognome",
                cardFullName = "Nome Cognome",
                cardNumber = "0000000000000000",
                cardExpireMonth = 0,
                cardExpireYear = 0,
                cardCVV = "000",
                uid = 0,
                lastOid = 0,
                orderStatus = "NONE"
            ) // Risultato in caso di eccezione
        }
    }

    suspend fun putUserNewInfo(sid: String, uid: Int, datiProfilo: ProfileData) {
        Log.d(TAG, "putUserNewInfo")

        val url = "${CommunicationController.BASE_URL}/user/$uid"

        try {
            val httpResponse = CommunicationController.genericRequest(
                url,
                CommunicationController.HttpMethod.PUT,
                requestBody = ProfileDataModifiedByUser(
                    firstName = datiProfilo.firstName?: "",
                    lastName = datiProfilo.lastName?: "",
                    cardFullName = datiProfilo.cardFullName?: "",
                    cardNumber = datiProfilo.cardNumber?: "",
                    cardExpireMonth = datiProfilo.cardExpireMonth?: 0,
                    cardExpireYear = datiProfilo.cardExpireYear?: 0,
                    cardCVV = datiProfilo.cardCVV?: "",
                    sid = sid
                )
            )

            // Controllo se la risposta è andata a buon fine
            if (httpResponse.status.value == 204) {
                // Accedi a sid e uid dal corpo della risposta
                Log.d(
                    TAG,
                    "La richiesta è andata a buon fine. HTTP Status: ${httpResponse.status.value}"
                )
            } else {
                // Se lo status non è 200, ritorniamo un valore di fallback
                Log.e(TAG, "Request failed. HTTP Status: ${httpResponse.status.value}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Request error: ${e.message}")
        }
    }

    suspend fun getMenuList(lat: Float, lng: Float, sid: String): List<Menu> {
        Log.d(TAG, "getMenuList")

        val url = "${CommunicationController.BASE_URL}/menu"

        return try {
            val httpResponse = CommunicationController.genericRequest(
                url,
                CommunicationController.HttpMethod.GET,
                queryParameters = mapOf("lat" to lat, "lng" to lng, "sid" to sid)
            )

            // Controllo se la risposta è andata a buon fine
            if (httpResponse.status.value == 200) {
                val result: List<Menu> = httpResponse.body()
                val menu = result[0]

                // Accedi a sid e uid dal corpo della risposta
                Log.d(TAG, "lista menu: {$menu}")

                return result
            } else {
                // Se lo status non è 200, ritorniamo un valore di fallback
                Log.e(TAG, "Request failed. HTTP Status: ${httpResponse.status.value}")
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
                ) // Risultato di fallback
            }
        } catch (e: Exception) {
            Log.e(TAG, "Request error: ${e.message}")
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
            ) // Risultato in caso di eccezione
        }
    }

    suspend fun getImageMenu(mid: Int, sid: String): ImageUI {
        Log.d(TAG, "getImageMenu")

        val url = "${CommunicationController.BASE_URL}/menu/${mid}/image"

        return try {
            val httpResponse = CommunicationController.genericRequest(
                url,
                CommunicationController.HttpMethod.GET,
                queryParameters = mapOf("sid" to sid)
            )

            // Controllo se la risposta è andata a buon fine
            if (httpResponse.status.value == 200) {
                val result: ImageUI = httpResponse.body()

                // Accedi a sid e uid dal corpo della risposta
                Log.d(TAG, "Immagine menu: ${result}")

                return result
            } else {
                // Se lo status non è 200, ritorniamo un valore di fallback
                Log.e(TAG, "Request failed. HTTP Status: ${httpResponse.status.value}")
                ImageUI("") // Risultato di fallback
            }
        } catch (e: Exception) {
            Log.e(TAG, "Request error: ${e.message}")
            ImageUI("") // Risultato in caso di eccezione
        }
    }

    suspend fun getMenuDitails(mid: Int): MenuDitails {
        Log.d(TAG, "getMenuDitails")
        Log.d("getMenuDitails", "MID: $mid")

        val url = "${CommunicationController.BASE_URL}/menu/$mid"

        return try {
            val httpResponse = CommunicationController.genericRequest(
                url,
                CommunicationController.HttpMethod.GET
            )

            // Controllo se la risposta è andata a buon fine
            if (httpResponse.status.value == 200) {
                val result: MenuDitails = httpResponse.body()

                // Accedi a sid e uid dal corpo della risposta
                Log.d(TAG, "Dettagli menu: ${result}")

                return result
            } else {
                // Se lo status non è 200, ritorniamo un valore di fallback
                Log.e(TAG, "Request failed. HTTP Status: ${httpResponse.status.value}")
                MenuDitails(
                    mid = mid,
                    name = "Nome menu",
                    price = 0.0,
                    location = mapOf("lat" to 0f, "lng" to 0f),
                    imageVersion = 0,
                    shortDescription = "Descrizione menu",
                    deliveryTime = 0,
                    longDescription = "Descrizione lunga menu"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Request error: ${e.message}")
            MenuDitails(
                mid = mid,
                name = "Nome menu",
                price = 0.0,
                location = mapOf("lat" to 0f, "lng" to 0f),
                imageVersion = 0,
                shortDescription = "Descrizione menu",
                deliveryTime = 0,
                longDescription = "Descrizione lunga menu"
            ) // Risultato di fallback in caso di eccezione
        }
    }

    suspend fun postMenu(mid: Int, sid: String, cardNumber: String?, lat: Float, lng: Float): Order_ON_DELIVERY {
        Log.d(TAG, "postMenu")
        Log.d("postMenu", "MID: $mid SID: $sid LAT: $lat LNG: $lng")

        val url = "${CommunicationController.BASE_URL}/order/${mid}/buy"

        return try {
            val httpResponse =
                CommunicationController.genericRequest(
                    url,
                    CommunicationController.HttpMethod.POST,
                    requestBody = BodyOrder( sid = sid, cardNumber = cardNumber, deliveryLocation = Location(lat, lng) )
                )

            Log.d("postMenu", "Response: ${httpResponse}")

            // Controllo se la risposta è andata a buon fine
            if (httpResponse.status.value == 200) {
                val result: Order_ON_DELIVERY = httpResponse.body()
                Log.d(TAG, "Ordine creato: $result")

                return result
            } else if (httpResponse.status.value == 403) {
                Log.e(TAG, "Request failed. HTTP Status 403: ${httpResponse.status.value}")

                Order_ON_DELIVERY(
                    oid = 0,
                    mid = 0,
                    uid = 0,
                    creationTimestamp = "",
                    status = "INVALID_CARD",
                    deliveryLocation = Location(0f, 0f),
                    expectedDeliveryTimestamp = "",
                    currentPosition = Location(0f, 0f)
                )// Risultato di fallback
            } else if (httpResponse.status.value == 409) {
                Log.e(TAG, "Request failed. HTTP Status 409: ${httpResponse.status.value}")

                Order_ON_DELIVERY(
                    oid = 0,
                    mid = 0,
                    uid = 0,
                    creationTimestamp = "",
                    status = "ORDER_ALREADY_ON_DELIVERY",
                    deliveryLocation = Location(0f, 0f),
                    expectedDeliveryTimestamp = "",
                    currentPosition = Location(0f, 0f)
                )// Risultato di fallback
            } else {
                // Se lo status non è 200, ritorniamo un valore di fallback
                Log.e(TAG, "Request failed. HTTP Status: ${httpResponse.status.value}")
                Order_ON_DELIVERY(
                    oid = 0,
                    mid = 0,
                    uid = 0,
                    creationTimestamp = "",
                    status = "NULL",
                    deliveryLocation = Location(0f, 0f),
                    expectedDeliveryTimestamp = "",
                    currentPosition = Location(0f, 0f)
                ) // Risultato di fallback
            }
        } catch (e: Exception) {
            Log.e(TAG, "Request error: ${e.message}")
            Order_ON_DELIVERY(
                oid = 0,
                mid = 0,
                uid = 0,
                creationTimestamp = "",
                status = "NULL",
                deliveryLocation = Location(0f, 0f),
                expectedDeliveryTimestamp = "",
                currentPosition = Location(0f, 0f)
            )// Risultato di fallback in caso di eccezione
        }
    }

    suspend fun getOrder(oid: Int, sid: String): Order {
        Log.d(TAG, "getOrder $oid")

        val url = "${CommunicationController.BASE_URL}/order/$oid"

        return try {
            val httpResponse = CommunicationController.genericRequest(
                url,
                CommunicationController.HttpMethod.GET,
                queryParameters = mapOf("sid" to sid)
            )

            val statusCode = httpResponse.status.value
            val jsonString = httpResponse.bodyAsText()

            Log.d(TAG, "HTTP $statusCode")
            Log.d(TAG, "Raw JSON: $jsonString")

            if (statusCode != 200 || jsonString.isBlank()) {
                Log.e(TAG, "Invalid response → returning NULL order")
                return Order_NULL()
            }

            val json = Json { ignoreUnknownKeys = true }
            val jsonObject = json.parseToJsonElement(jsonString).jsonObject

            val jsonElement = json.parseToJsonElement(jsonString)

            Log.d(TAG, "JSON OBJECT (pretty): ${jsonElement}")

            return when {

                jsonObject.containsKey("deliveryTimestamp") -> {
                    Log.d(TAG, "Detected COMPLETED → deliveryTimestamp key FOUND")
                    Log.d(TAG, "Decoding as Order_COMPLETED")
                    Json.decodeFromJsonElement<Order_COMPLETED>(jsonObject)
                }

                jsonObject.containsKey("expectedDeliveryTimestamp") -> {
                    Log.d(TAG, "Detected ON_DELIVERY → expectedDeliveryTimestamp key FOUND")
                    Log.d(TAG, "Decoding as Order_ON_DELIVERY")
                    Json.decodeFromJsonElement<Order_ON_DELIVERY>(jsonObject)
                }

                else -> {
                    Log.w(TAG, "No timestamp keys found → decoding as generic Order")
                    Json.decodeFromJsonElement<Order>(jsonObject)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Request error: ${e.message}")
            return Order_NULL()
        }
    }

}