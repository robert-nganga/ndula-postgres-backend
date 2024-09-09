package com.robert.models

data class ShippingAddress(
    val id: Int,
    val name: String,
    val formattedAddress: String,
    val lat: Double,
    val lng: Double,
    val placeId: String,
    val phoneNumber: String,
    val floorNumber: String,
    val doorNumber: String,
    val buildingName: String
)
