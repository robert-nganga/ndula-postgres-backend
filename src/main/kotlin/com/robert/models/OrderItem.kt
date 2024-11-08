package com.robert.models


data class OrderItem(
    val id: Int,
    val orderId: Int,
    val shoe: Shoe,
    val variantId: Int,
    val quantity: Int,
    val price: Double,
    val rating: Double? = null
)
