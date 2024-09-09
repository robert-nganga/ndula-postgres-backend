package com.robert.location_services

import com.google.maps.model.LatLng
import com.robert.response.PlaceDetailsResponse

interface LocationService {

    suspend fun getPlaceSuggestions(query: String): List<PlaceDetailsResponse>

    fun getPlaceDetailsFromCoordinates(latLng: LatLng): PlaceDetailsResponse
}