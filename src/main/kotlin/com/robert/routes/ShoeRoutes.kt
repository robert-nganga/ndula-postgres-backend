package com.robert.routes

import com.robert.db.dao.shoe.ShoeDao
import com.robert.request.ShoeRequest
import com.robert.request.toShoe
import com.robert.response.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.shoeRoutes(
    shoeDao: ShoeDao,
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
        val insertedShoe = shoeDao.insertShoe(shoe)
        if (insertedShoe != null) {
            call.respond(HttpStatusCode.Created, insertedShoe)
        } else {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value))
        }
    }

    get("/all") {
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 10
        val pagedShoes = shoeDao.getAllShoesPaginated(page, pageSize)
        call.respond(pagedShoes)
    }

    get("/{id}") {
        val shoeId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val shoe = shoeDao.getShoeById(shoeId)
        if (shoe != null) {
            call.respond(HttpStatusCode.OK,shoe)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    get("/brand") {
        val brand = call.request.queryParameters["brand"]
        val shoes = if (brand != null) {
            shoeDao.filterShoesByBrand(brand)
        } else {
            emptyList()
        }
        call.respond(shoes)
    }

    get("/category") {
        val category = call.request.queryParameters["category"]
        val shoes = if (category != null) {
            shoeDao.filterShoesByCategory(category)
        } else {
            emptyList()
        }
        call.respond(shoes)
    }
}