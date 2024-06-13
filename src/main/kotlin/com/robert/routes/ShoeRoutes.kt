package com.robert.routes

import com.robert.repositories.shoe.ShoeRepository
import com.robert.request.ShoeRequest
import com.robert.request.toShoe
import com.robert.response.ErrorResponse
import com.robert.utils.BaseResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.shoeRoutes(
    shoeRepository: ShoeRepository,
){
    post("/add") {
        val request = call.receiveNullable<ShoeRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@post
        }
        val shoe = request.toShoe()
        val result = shoeRepository.insertShoe(shoe)
        when(result){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    get("/all") {
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 15
        val results = shoeRepository.getAllShoesPaginated(page, pageSize)
        when(results){
            is BaseResponse.SuccessResponse -> call.respond(results.status, results.data!!)
            is BaseResponse.ErrorResponse -> call.respond(results.status, results)
        }
    }

    get("/search") {
        val query = call.request.queryParameters["query"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val results = shoeRepository.searchShoes(query)
        when(results){
            is BaseResponse.SuccessResponse -> call.respond(results.status, results.data!!)
            is BaseResponse.ErrorResponse -> call.respond(results.status, results)
        }
    }

    get("/{id}") {
        val shoeId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val results = shoeRepository.getShoeById(shoeId)
        when(results){
            is BaseResponse.SuccessResponse -> call.respond(results.status, results.data!!)
            is BaseResponse.ErrorResponse -> call.respond(results.status, results)
        }
    }

    get("/brand") {
        val brand = call.request.queryParameters["brand"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val result = shoeRepository.filterShoesByBrand(brand)
        when(result){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    get("/category") {
        val category = call.request.queryParameters["category"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val result = shoeRepository.filterShoesByCategory(category)
        when(result){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }
}