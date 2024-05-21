package com.robert.models

data class Order (
    val id: Int,
    val userId: Int,
    val items: List<OrderItem>,
    val total: Double,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)
