package com.robert.routes

import com.robert.db.dao.cart.CartDao
import com.robert.request.CartItemRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.cartRoutes(
    cartDao: CartDao
){
    post("/add") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim("userId", String::class)
        if(userId == null){
            call.respond(HttpStatusCode.Conflict, mapOf("message" to "User not found"))
            return@post
        }

        val request = call.receiveNullable<CartItemRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid request"))
            return@post
        }

        val cart = cartDao.addItemToCart(cartId = request.cartId, shoeId = request.shoeId, quantity = request.quantity)
        call.respond(HttpStatusCode.OK, cart)
    }
}