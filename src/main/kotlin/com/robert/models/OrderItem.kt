package com.robert.models


data class OrderItem(
    val id: Int,
    val orderId: Int,
    val shoe: Shoe,
    val quantity: Int,
    val price: Double
)
