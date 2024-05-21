package com.robert.repositories.order

import com.robert.models.Order
import com.robert.request.OrderRequest
import com.robert.utils.BaseResponse

interface OrderRepository {
    suspend fun createOrder(orderRequest: OrderRequest): BaseResponse<Order>
    suspend fun getOrderById(orderId: Int): BaseResponse<Order>
    suspend fun getAllOrdersForUser(userId: Int): BaseResponse<List<Order>>
    suspend fun updateOrderStatus(orderId: Int, newStatus: String): BaseResponse<Order>
}