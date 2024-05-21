package com.robert.routes

import com.robert.db.dao.order.OrderDao
import com.robert.request.OrderRequest
import com.robert.request.UpdateOrderStatusRequest
import com.robert.response.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.orderRoutes(
    orderDao: OrderDao
) {

    post("/add") {
        val request = call.receiveNullable<OrderRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@post
        }

        val order = orderDao.createOrder(request)
        call.respond(HttpStatusCode.Created, order)
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

        val oder = orderDao.updateOrderStatus(orderId = request.orderId, newStatus = request.status)
        call.respond(HttpStatusCode.OK, oder)
        return@post
    }

    get("/all/{id}"){
        val userId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val orders = orderDao.getAllOrdersForUser(userId)
        call.respond(HttpStatusCode.OK, orders)
        return@get
    }

    get("/{id}"){
        val orderId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val order = orderDao.getOrderById(orderId)
        call.respond(HttpStatusCode.OK, order)
    }
}
