package com.robert.routes

import com.robert.models.Brand
import com.robert.repositories.shoe.BrandRepository
import com.robert.request.BrandRequest
import com.robert.response.ErrorResponse
import com.robert.utils.BaseResponse
import com.robert.utils.getUserIdFromAuthToken
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.brandRoutes(
    brandRepository: BrandRepository
) {
    post("/add") {
        val request = call.receiveNullable<BrandRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@post
        }

        val requestData = Brand(
            id = 0,
            name = request.name,
            description = request.description,
            logoUrl = request.logo,
        )
        val result = brandRepository.insertBrand(requestData)
        when(result){
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
        }
        return@post
    }

    get("/{brand}"){
        val brand = call.parameters["brand"] ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@get
        }
        val query = call.request.queryParameters["query"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val userId = getUserIdFromAuthToken() ?: kotlin.run {
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }
        val result = brandRepository.searchShoes(brand, query, userId)
        when(result){
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
        }
    }

    get("/all") {
        val result = brandRepository.getAllBrands()
        when(result){
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
        }
    }
}