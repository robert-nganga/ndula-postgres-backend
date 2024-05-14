package com.robert.routes

import com.robert.db.dao.category.CategoryDao
import com.robert.models.Category
import com.robert.request.CategoryRequest
import com.robert.response.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoryRoutes(
    categoryDao: CategoryDao
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
        val category = categoryDao.insertCategory(requestData)

        if (category == null){
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Error while adding category", "", HttpStatusCode.InternalServerError.value)
            )
        } else {
            call.respond(HttpStatusCode.Created, category)
        }
    }

    get("/all") {
        val categories = categoryDao.getAllCategories()
        call.respond(HttpStatusCode.OK, categories)
    }
}