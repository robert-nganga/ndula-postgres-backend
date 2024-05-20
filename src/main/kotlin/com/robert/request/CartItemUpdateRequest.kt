package com.robert.request

data class CartItemUpdateRequest(
    val cartItemId: Int,
    val cartId: Int,
    val quantity: Int
)
