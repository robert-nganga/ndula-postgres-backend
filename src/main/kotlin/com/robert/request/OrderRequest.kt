package com.robert.request

import com.robert.models.OrderStatus
import com.robert.models.ShippingAddress

data class OrderRequest(
    val items: List<OrderItemRequest>,
    val totalAmount: Double,
    val status: OrderStatus,
    val shippingAddress: ShippingAddress,
)