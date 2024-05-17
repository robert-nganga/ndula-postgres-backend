package com.robert.request

data class CartItemRequest(
    val cartId: Int,
    val shoeId: Int,
    val quantity: Int
)
