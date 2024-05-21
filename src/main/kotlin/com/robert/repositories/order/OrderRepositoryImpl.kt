package com.robert.repositories.order

import com.robert.db.dao.order.OrderDao
import com.robert.models.Order
import com.robert.request.OrderRequest
import com.robert.utils.BaseResponse
import io.ktor.http.*

class OrderRepositoryImpl(
    private val orderDao: OrderDao
) : OrderRepository {

    override suspend fun createOrder(orderRequest: OrderRequest): BaseResponse<Order> {
        return try {
            val order = orderDao.createOrder(orderRequest)
            BaseResponse.SuccessResponse(data = order, status = HttpStatusCode.Created)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(
                message = "An error occurred while creating the order: ${e.message}",
                status = HttpStatusCode.InternalServerError
            )
        }
    }

    override suspend fun getOrderById(orderId: Int): BaseResponse<Order> {
        return try {
            val order = orderDao.getOrderById(orderId)
            BaseResponse.SuccessResponse(data = order)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(
                message = "An error occurred while fetching the order: ${e.message}",
                status = HttpStatusCode.InternalServerError
            )
        }
    }

    override suspend fun getAllOrdersForUser(userId: Int): BaseResponse<List<Order>> {
        return try {
            val orders = orderDao.getAllOrdersForUser(userId)
            BaseResponse.SuccessResponse(data = orders)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(
                message = "An error occurred while fetching orders for the user: ${e.message}",
                status = HttpStatusCode.InternalServerError
            )
        }
    }

    override suspend fun updateOrderStatus(orderId: Int, newStatus: String): BaseResponse<Order> {
        return try {
            val updatedOrder = orderDao.updateOrderStatus(orderId, newStatus)
            BaseResponse.SuccessResponse(data = updatedOrder)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(
                message = "An error occurred while updating the order status: ${e.message}",
                status = HttpStatusCode.InternalServerError
            )
        }
    }
}