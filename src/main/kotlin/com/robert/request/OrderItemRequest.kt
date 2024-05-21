package com.robert.request

import com.robert.models.Shoe

data class OrderItemRequest(
    val orderId: Int,
    val shoeId: Int,
    val quantity: Int,
    val price: Double
)
