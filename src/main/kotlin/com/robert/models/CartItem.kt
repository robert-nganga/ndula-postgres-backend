package com.robert.models

data class CartItem(
    val id: Int,
    val cartId: Int,
    val shoeId: Int,
    val quantity: Int
)