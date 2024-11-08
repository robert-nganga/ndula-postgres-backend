package com.robert.routes

import com.robert.models.Shoe
import com.robert.models.ShoeFilterOptions
import com.robert.repositories.shoe.ShoeRepository
import com.robert.request.ShoeRequest
import com.robert.response.ErrorResponse
import com.robert.utils.BaseResponse
import com.robert.utils.getUserIdFromAuthToken
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

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
        when(val result = shoeRepository.insertShoe(request)){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    get("/all") {
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 15
        val userId = getUserIdFromAuthToken()
        when(val results = shoeRepository.getAllShoesPaginated(page, pageSize, userId)){
            is BaseResponse.SuccessResponse -> call.respond(results.status, results.data!!)
            is BaseResponse.ErrorResponse -> call.respond(results.status, results)
        }
    }

    get("/filter") {
        val filterOptions = ShoeFilterOptions(
            category = call.request.queryParameters["category"],
            brand = call.request.queryParameters["brand"],
            minPrice = call.request.queryParameters["minPrice"]?.toDoubleOrNull(),
            maxPrice = call.request.queryParameters["maxPrice"]?.toDoubleOrNull(),
            sortBy = call.request.queryParameters["sortBy"] ?: "price",
            sortOrder = call.request.queryParameters["sortOrder"]?.lowercase(Locale.getDefault()) ?: "asc",
            page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1,
            pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 15
        )
        val userId = getUserIdFromAuthToken()

        when (val result = shoeRepository.getFilteredAndSortedShoes(filterOptions, userId)) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    get("/search") {
        val query = call.request.queryParameters["query"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val userId = getUserIdFromAuthToken()
        when(val results = shoeRepository.searchShoes(query, userId)){
            is BaseResponse.SuccessResponse -> call.respond(results.status, results.data!!)
            is BaseResponse.ErrorResponse -> call.respond(results.status, results)
        }
    }

    get("/{id}") {
        val shoeId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val userId = getUserIdFromAuthToken()
        when(val results = shoeRepository.getShoeById(shoeId, userId)){
            is BaseResponse.SuccessResponse -> call.respond(results.status, results.data!!)
            is BaseResponse.ErrorResponse -> call.respond(results.status, results)
        }
    }

    get("/brand") {
        val brand = call.request.queryParameters["brand"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val userId = getUserIdFromAuthToken()
        when(val result = shoeRepository.filterShoesByBrand(brand, userId)){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    get("/category") {
        val category = call.request.queryParameters["category"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val userId = getUserIdFromAuthToken()
        when(val result = shoeRepository.filterShoesByCategory(category, userId)){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    delete("delete/{id}") {
        val shoeId = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
        when (val result = shoeRepository.deleteShoe(shoeId)) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    put("/update") {
        val request = call.receiveNullable<Shoe>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@put
        }
        when (val result = shoeRepository.updateShoe(request)) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }
}