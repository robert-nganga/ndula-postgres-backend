package com.robert.routes

import com.robert.models.Category
import com.robert.repositories.shoe.CategoryRepository
import com.robert.request.CategoryRequest
import com.robert.response.ErrorResponse
import com.robert.utils.BaseResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoryRoutes(
    categoryRepository: CategoryRepository
) {
    post("/add") {
        val request = call.receiveNullable<CategoryRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@post
        }
        val requestData = Category(
            id = 0,
            name = request.name,
            description = request.description,
        )
        val result = categoryRepository.insertCategory(requestData)
        when(result) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    get("/all") {
        val result = categoryRepository.getAllCategories()
        when(result) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }
}