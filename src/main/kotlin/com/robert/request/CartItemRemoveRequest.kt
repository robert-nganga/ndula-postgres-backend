package com.robert.request

data class CartItemRemoveRequest(
    val cartItemId: Int,
    val cartId: Int,
)
