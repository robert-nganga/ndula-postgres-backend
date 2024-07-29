package com.robert.routes

import com.robert.repositories.wish_list.WishListRepository
import com.robert.utils.BaseResponse
import com.robert.utils.getUserIdFromAuthToken
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.wishlistRoutes(wishlistRepository: WishListRepository) {
    get("/my_wishlist") {
        val userId = getUserIdFromAuthToken()
            ?: return@get call.respond(HttpStatusCode.Unauthorized)

        when (val result = wishlistRepository.getWishlistByUserId(userId)) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    post("/add/{shoeId}") {
        val userId = getUserIdFromAuthToken()
            ?: return@post call.respond(HttpStatusCode.Unauthorized)

        val shoeId = call.parameters["shoeId"]?.toIntOrNull()
            ?: return@post call.respond(HttpStatusCode.BadRequest)

        when (val result = wishlistRepository.addItemToWishlist(userId, shoeId)) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    delete("/remove/{shoeId}") {
        val userId = getUserIdFromAuthToken()
            ?: return@delete call.respond(HttpStatusCode.Unauthorized)

        val shoeId = call.parameters["shoeId"]?.toIntOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest)

        when (val result = wishlistRepository.removeItemFromWishlist(userId, shoeId)) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    delete("/clear") {
        val userId = getUserIdFromAuthToken()
            ?: return@delete call.respond(HttpStatusCode.Unauthorized)

        when (val result = wishlistRepository.clearWishlist(userId)) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }
}