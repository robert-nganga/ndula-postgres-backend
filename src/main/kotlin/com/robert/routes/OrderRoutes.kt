package com.robert.routes

import com.robert.db.dao.order.OrderDao
import com.robert.repositories.order.OrderRepository
import com.robert.request.OrderRequest
import com.robert.request.UpdateOrderStatusRequest
import com.robert.response.ErrorResponse
import com.robert.utils.BaseResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.orderRoutes(
    orderRepository: OrderRepository
) {

    post("/add") {
        val request = call.receiveNullable<OrderRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@post
        }

        val result = orderRepository.createOrder(request)
        when(result){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
        return@post
    }

    post("/update") {
        val request = call.receiveNullable<UpdateOrderStatusRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@post
        }

        val result = orderRepository.updateOrderStatus(orderId = request.orderId, newStatus = request.status)
        when(result){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
        return@post
    }

    get("/all/{id}"){
        val userId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val result = orderRepository.getAllOrdersForUser(userId)
        when(result){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
        return@get
    }

    get("/{id}"){
        val orderId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val result = orderRepository.getOrderById(orderId)
        when(result){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
        return@get
    }
}
