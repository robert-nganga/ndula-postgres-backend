package com.robert.repositories.order

import com.robert.models.Order
import com.robert.models.OrderStatus
import com.robert.request.OrderRequest
import com.robert.utils.BaseResponse

interface OrderRepository {
    suspend fun createOrder(order: OrderRequest, currentUserId: Int): BaseResponse<Order>
    suspend fun getOrderById(id: Int): BaseResponse<Order>
    suspend fun updateOrderStatus(id: Int, status: OrderStatus): BaseResponse<Boolean>
    suspend fun getOrdersForUser(userId: Int): BaseResponse<List<Order>>
    suspend fun getActiveOrders(userId: Int): BaseResponse<List<Order>>
    suspend fun getCompletedOrders(userId: Int): BaseResponse<List<Order>>
}