package com.example.valentinabotti_kotlin.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.http.HttpStatusCode
import androidx.core.net.toUri

object CommunicationController {
    //val BASE_URL = "http://10.0.2.2:5077/api"//localhost
    val BASE_URL = "http://192.168.1.16:5077/api"
    var sid: String? = null
    private val TAG = CommunicationController::class.simpleName

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    enum class HttpMethod {
        GET,
        POST,
        DELETE,
        PUT
    }

    // Funzione generica per effettuare richieste HTTP
    suspend fun genericRequest(
        url: String,
        method: HttpMethod,
        queryParameters: Map<String, Any> = emptyMap(),
        requestBody: Any? = null
    ): HttpResponse {
        val urlUri = url.toUri()
        val urlBuilder = urlUri.buildUpon()
        queryParameters.forEach { (key, value) ->
            urlBuilder.appendQueryParameter(key, value.toString())
        }
        val completeUrlString = urlBuilder.build().toString()
        Log.d(TAG, "Request URL: $completeUrlString")

        val request: HttpRequestBuilder.() -> Unit = {
            requestBody?.let {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        }

        // Esegui la richiesta HTTP
        val result = try {
            when (method) {
                HttpMethod.GET -> client.get(completeUrlString, request)
                HttpMethod.POST -> client.post(completeUrlString, request)
                HttpMethod.DELETE -> client.delete(completeUrlString, request)
                HttpMethod.PUT -> client.put(completeUrlString, request)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Request error: ${e.message}")
            throw e  // Rilancia l'eccezione per gestirla più in alto
        }

        // Controllo dello status code
        handleResponseStatus(result)

        return result
    }

    private fun handleResponseStatus(response: HttpResponse) {
        when (response.status) {
            HttpStatusCode.OK -> Log.d(TAG, "Request successful. HTTP Status: 200 OK")
            HttpStatusCode.NoContent -> Log.d(TAG, "No content available. HTTP Status: 204 No Content")
            HttpStatusCode.Conflict -> Log.e(TAG, "Conflict. HTTP Status: 409 Conflict")
            HttpStatusCode.Unauthorized -> Log.e(TAG, "Invalid SID. HTTP Status: 401 Unauthorized")
            HttpStatusCode.NotFound -> Log.e(TAG, "Not found. HTTP Status: 404 Not Found")
            HttpStatusCode.Forbidden -> Log.e(TAG, "Invalid card. HTTP Status: 403 Forbidden")
            else -> Log.e(TAG, "Unexpected status code: ${response.status.value}")
        }
    }
}