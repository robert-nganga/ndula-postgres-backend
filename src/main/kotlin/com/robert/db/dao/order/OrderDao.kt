package com.robert.db.dao.order

import com.robert.models.Order
import com.robert.request.OrderRequest
import java.math.BigDecimal

interface OrderDao {
    suspend fun createOrder(orderRequest: OrderRequest): Order
    suspend fun getOrderById(orderId: Int): Order
    suspend fun getAllOrdersForUser(userId: Int): List<Order>
    suspend fun updateOrderStatus(orderId: Int, newStatus: String): Order
}