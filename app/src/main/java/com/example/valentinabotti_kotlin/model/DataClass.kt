package com.example.valentinabotti_kotlin.model

import kotlinx.serialization.Serializable//pacchetto kotlin per la serializzazione e deserializzazione di dati

@Serializable
data class UidSid(
    val sid: String?,
    val uid: Int?
)

@Serializable//abilita una classe a essere serializzata e deserializzata (convertita in JSON o altri formati)
data class ProfileData(
    val firstName: String?,
    val lastName: String?,
    val cardFullName: String?,
    val cardNumber: String?,
    val cardExpireMonth: Int?,
    val cardExpireYear: Int?,
    val cardCVV: String?,
    val uid: Int?,
    val lastOid: Int?,
    val orderStatus: String?
)

@Serializable
data class ProfileDataString(
    val firstName: String?,
    val lastName: String?,
    val cardFullName: String?,
    val cardNumber: String?,
    val cardExpireMonth: String,
    val cardExpireYear: String,
    val cardCVV: String?,
    val uid: Int?,
    val lastOid: Int?,
    val orderStatus: String?
)

@Serializable
data class ProfileDataModifiedByUser(
    val firstName: String,
    val lastName: String,
    val cardFullName: String,
    val cardNumber: String,
    val cardExpireMonth: Int,
    val cardExpireYear: Int,
    val cardCVV: String,
    val sid: String
)

@Serializable
data class Menu(
    val mid: Int,
    val name: String,
    val price: Double,
    val location: Map<String, Double>,
    val imageVersion: Int,
    val shortDescription: String,
    val deliveryTime: Int
)

@Serializable
data class MenuDitails(
    val mid: Int,
    val name: String,
    val price: Double,
    val location: Map<String, Float>,
    val imageVersion: Int,
    val shortDescription: String,
    val longDescription: String,
    val deliveryTime: Int
)

@Serializable
sealed class Order {
    abstract val oid: Int
    abstract val mid: Int
    abstract val uid: Int
    abstract val creationTimestamp: String
    abstract val status: String
    abstract val deliveryLocation: Location
    abstract val currentPosition: Location
}

@Serializable
data class Order_COMPLETED(
    override val oid: Int,
    override val mid: Int,
    override val uid: Int,
    override val creationTimestamp: String,
    override val status: String,
    override val deliveryLocation: Location,
    override val currentPosition: Location,
    val deliveryTimestamp: String // Aggiunta specifica di Order_COMPLETED
) : Order()

@Serializable
data class Order_ON_DELIVERY(
    override val oid: Int,
    override val mid: Int,
    override val uid: Int,
    override val creationTimestamp: String,
    override val status: String,
    override val deliveryLocation: Location,
    override val currentPosition: Location,
    val expectedDeliveryTimestamp: String // Aggiunta specifica di Order_ON_DELIVERY
) : Order()

@Serializable
data class ImageUI (
    val base64: String
)

@Serializable
data class Location(
    val lat: Float,
    val lng: Float
)

@Serializable
data class BodyOrder(
    val sid: String,
    val deliveryLocation: Location,
    val cardNumber: String?
)