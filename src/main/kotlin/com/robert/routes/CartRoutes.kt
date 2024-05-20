package com.robert.routes

import com.robert.db.dao.cart.CartDao
import com.robert.repositories.cart.CartRepository
import com.robert.request.CartItemAddRequest
import com.robert.request.CartItemRemoveRequest
import com.robert.request.CartItemUpdateRequest
import com.robert.utils.BaseResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.cartRoutes(
    cartRepository: CartRepository
){
    post("/add") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim("userId", String::class)
        if(userId == null){
            call.respond(HttpStatusCode.Conflict, mapOf("message" to "User not found"))
            return@post
        }

        val request = call.receiveNullable<CartItemAddRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid request"))
            return@post
        }

        val result = cartRepository.addItemToCart(cartId = request.cartId, shoeId = request.shoeId, quantity = request.quantity)
        when(result){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
        return@post
    }

    post("/remove") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim("userId", String::class)
        if(userId == null){
            call.respond(HttpStatusCode.Conflict, mapOf("message" to "User not found"))
            return@post
        }

        val request = call.receiveNullable<CartItemRemoveRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid request"))
            return@post
        }

        val cart = cartRepository.removeItemFromCart(cartId = request.cartId, cartItemId = request.cartItemId)
        call.respond(HttpStatusCode.OK, cart)
    }

    post("/update") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim("userId", String::class)
        if(userId == null){
            call.respond(HttpStatusCode.Conflict, mapOf("message" to "User not found"))
            return@post
        }

        val request = call.receiveNullable<CartItemUpdateRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid request"))
            return@post
        }

        val cart = cartRepository.updateCartItemQuantity(cartId = request.cartId, cartItemId = request.cartItemId, newQuantity = request.quantity)
        call.respond(HttpStatusCode.OK, cart)
    }

    get("/{id}"){
        val cartId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)

        val cart = cartRepository.getCartById(cartId)
        call.respond(HttpStatusCode.OK, cart)
    }
}