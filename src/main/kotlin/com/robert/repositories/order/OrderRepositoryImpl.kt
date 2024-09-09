package com.robert.repositories.order

import com.robert.db.dao.order.InsufficientInventoryException
import com.robert.db.dao.order.OrderDao
import com.robert.models.Order
import com.robert.models.OrderStatus
import com.robert.request.OrderRequest
import com.robert.utils.BaseResponse
import io.ktor.http.*

class OrderRepositoryImpl(
    private val orderDao: OrderDao
) : OrderRepository {
    override suspend fun createOrder(order: OrderRequest, currentUserId: Int): BaseResponse<Order> {
        return try {
            val createdOrder = orderDao.createOrder(order, currentUserId)
            BaseResponse.SuccessResponse(data = createdOrder, status = HttpStatusCode.Created)
        } catch (e: InsufficientInventoryException) {
            BaseResponse.ErrorResponse(e.message, HttpStatusCode.BadRequest)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getOrderById(id: Int): BaseResponse<Order> {
        return try {
            val order = orderDao.getOrderById(id)
            BaseResponse.SuccessResponse(data = order)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun updateOrderStatus(id: Int, status: OrderStatus): BaseResponse<Boolean> {
        return try {
            val updated = orderDao.updateOrderStatus(id, status)
            if (updated) {
                BaseResponse.SuccessResponse(data = true)
            } else {
                BaseResponse.ErrorResponse("Failed to update order status", HttpStatusCode.BadRequest)
            }
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getOrdersForUser(userId: Int): BaseResponse<List<Order>> {
        return try {
            val orders = orderDao.getOrdersForUser(userId)
            BaseResponse.SuccessResponse(data = orders)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getActiveOrders(userId: Int): BaseResponse<List<Order>> {
        return try {
            val orders = orderDao.getActiveOrders(userId)
            BaseResponse.SuccessResponse(data = orders)
        } catch (e: Exception){
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getCompletedOrders(userId: Int): BaseResponse<List<Order>> {
        return try{
            val orders = orderDao.getCompletedOrders(userId)
            BaseResponse.SuccessResponse(data = orders)
        } catch (e: Exception){
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }
}