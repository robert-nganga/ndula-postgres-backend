package com.robert.response

import com.google.maps.model.PlaceDetails

data class PlaceDetailsResponse(
    val name: String,
    val formattedAddress: String,
    val lat: Double,
    val lng: Double,
    val placeId: String
)

fun PlaceDetails.toPlaceDetailsResponse(): PlaceDetailsResponse{
    return PlaceDetailsResponse(
        name = name,
        formattedAddress = formattedAddress,
        lat = geometry.location.lat,
        lng = geometry.location.lng,
        placeId = placeId
    )
}
