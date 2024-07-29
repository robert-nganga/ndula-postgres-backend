package com.robert.routes

import com.robert.repositories.wish_list.WishListRepository
import com.robert.utils.BaseResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.wishlistRoutes(wishlistRepository: WishListRepository) {
    route("/my_wishlist") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.Unauthorized)

            when (val result = wishlistRepository.getWishlistByUserId(userId)) {
                is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
                is BaseResponse.ErrorResponse -> call.respond(result.status, result)
            }
        }

        post("/add/{shoeId}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.Unauthorized)

            val shoeId = call.parameters["shoeId"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            when (val result = wishlistRepository.addItemToWishlist(userId, shoeId)) {
                is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
                is BaseResponse.ErrorResponse -> call.respond(result.status, result)
            }
        }

        delete("/remove/{shoeId}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.Unauthorized)

            val shoeId = call.parameters["shoeId"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            when (val result = wishlistRepository.removeItemFromWishlist(userId, shoeId)) {
                is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
                is BaseResponse.ErrorResponse -> call.respond(result.status, result)
            }
        }

        delete("/clear") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.Unauthorized)

            when (val result = wishlistRepository.clearWishlist(userId)) {
                is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
                is BaseResponse.ErrorResponse -> call.respond(result.status, result)
            }
        }
    }
}