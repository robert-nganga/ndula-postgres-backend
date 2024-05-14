package com.robert.routes

import com.robert.db.dao.brand.BrandDao
import com.robert.models.Brand
import com.robert.request.BrandRequest
import com.robert.response.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.brandRoutes(
    brandDao: BrandDao
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
        val result = brandDao.insertBrand(requestData)

        if(result == null){
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Error while adding brand", "", HttpStatusCode.InternalServerError.value)
            )
        } else{
            call.respond(HttpStatusCode.Created, result)
        }
    }

    get("/all") {
        val result = brandDao.getAllBrands()
        call.respond(HttpStatusCode.OK, result)
    }
}