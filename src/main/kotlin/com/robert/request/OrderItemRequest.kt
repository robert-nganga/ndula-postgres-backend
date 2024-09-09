package com.robert.request


data class OrderItemRequest (
    val shoeId: Int,
    val variantId: Int,
    val quantity: Int,
    val price: Double
)


