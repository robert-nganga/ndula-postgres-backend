package com.robert.request

data class CartItemAddRequest(
    val cartId: Int,
    val shoeId: Int,
    val quantity: Int
)
