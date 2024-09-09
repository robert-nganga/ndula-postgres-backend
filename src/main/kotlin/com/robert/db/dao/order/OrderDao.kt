package com.robert.db.dao.order

import com.robert.models.Order
import com.robert.models.OrderStatus
import com.robert.request.OrderRequest

interface OrderDao {
    suspend fun createOrder(order: OrderRequest, currentUserId: Int): Order
    suspend fun getOrderById(id: Int): Order
    suspend fun updateOrderStatus(id: Int, status: OrderStatus): Boolean
    suspend fun getOrdersForUser(userId: Int): List<Order>
    suspend fun getActiveOrders(userId: Int): List<Order>
    suspend fun getCompletedOrders(userId: Int): List<Order>
    suspend fun getVariantQuantity(variantId: Int): Int
    suspend fun updateVariantQuantity(variantId: Int, newQuantity: Int): Int
}