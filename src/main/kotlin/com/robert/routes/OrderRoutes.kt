package com.robert.routes

import com.robert.repositories.order.OrderRepository
import com.robert.request.OrderRequest
import com.robert.response.ErrorResponse
import com.robert.utils.BaseResponse
import com.robert.utils.getUserIdFromAuthToken
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.orderRoutes(
    orderRepository: OrderRepository
) {

    post("/add") {
        val order = call.receiveNullable<OrderRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Bad request", "", HttpStatusCode.BadRequest.value)
            )
            return@post
        }
        val userId = getUserIdFromAuthToken()
            ?: return@post call.respond(HttpStatusCode.Unauthorized)

        when (val result = orderRepository.createOrder(order, userId)) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse ->  call.respond(result.status, result)
        }
    }

    get("/all"){
        val userId = getUserIdFromAuthToken()
            ?: return@get call.respond(HttpStatusCode.Unauthorized)

        when (val result = orderRepository.getOrdersForUser(userId)) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    get("/active"){
        val userId = getUserIdFromAuthToken()
            ?: return@get call.respond(HttpStatusCode.Unauthorized)

        when (val result = orderRepository.getActiveOrders(userId)) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    get("/completed"){
        val userId = getUserIdFromAuthToken()
            ?: return@get call.respond(HttpStatusCode.Unauthorized)

        when (val result = orderRepository.getCompletedOrders(userId)) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    get("/{id}"){
        val id = call.parameters["id"]?.toIntOrNull() ?:
            return@get call.respond(HttpStatusCode.BadRequest)
        when (val result = orderRepository.getOrderById(id)) {
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }
}
