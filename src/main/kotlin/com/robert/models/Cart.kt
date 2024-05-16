package com.robert.models

data class Cart(
    val id: Int,
    val items:  List<CartItem>,
    val createdAt: String,
    val updatedAt: String
)