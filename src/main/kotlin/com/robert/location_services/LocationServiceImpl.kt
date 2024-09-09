package com.robert.location_services

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.PlaceAutocompleteRequest
import com.google.maps.PlacesApi
import com.google.maps.model.LatLng
import com.google.maps.model.PlaceAutocompleteType
import com.robert.response.PlaceDetailsResponse
import com.robert.response.toPlaceDetailsResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class LocationServiceImpl(
    private val context: GeoApiContext
): LocationService {

    private val sessionToken = PlaceAutocompleteRequest.SessionToken()

    override suspend fun getPlaceSuggestions(query: String): List<PlaceDetailsResponse> {
        val request = PlacesApi.placeAutocomplete(context, query, sessionToken)
            .location(LatLng(-1.2924992584875004, 36.82069975883306))
            .radius(50000)
            .types(PlaceAutocompleteType.ESTABLISHMENT)

        val results = request.await()
       val places = coroutineScope {
           results.map { prediction ->
               async {
                   val placeDetails = PlacesApi.placeDetails(context, prediction.placeId)
                       .sessionToken(sessionToken)
                       .await()
                   placeDetails.toPlaceDetailsResponse()
               }
           }
       }.awaitAll()

        return places
    }

    override fun getPlaceDetailsFromCoordinates(latLng: LatLng): PlaceDetailsResponse {
        val request = GeocodingApi.reverseGeocode(context, latLng)

        val result = request.await()
        return PlacesApi.placeDetails(context, result.first().placeId)
            .sessionToken(sessionToken)
            .await()
            .toPlaceDetailsResponse()
    }
}