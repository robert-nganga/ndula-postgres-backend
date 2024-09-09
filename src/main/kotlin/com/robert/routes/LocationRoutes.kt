package com.robert.routes

import com.google.maps.model.LatLng
import com.robert.location_services.LocationService
import com.robert.request.LatLngRequest
import com.robert.response.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.locationRoutes(
    locationService: LocationService
){

    get("/autocomplete"){
        val query = call.request.queryParameters["query"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val result = locationService.getPlaceSuggestions(query)
        call.respond(HttpStatusCode.OK, result)
    }

    post("/reverseGeocoding"){
        val request = call.receiveNullable<LatLngRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@post
        }
        val latLng = LatLng(request.lat, request.lng)
        val result = locationService.getPlaceDetailsFromCoordinates(latLng)
        call.respond(HttpStatusCode.OK, result)
    }
}