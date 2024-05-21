package com.robert.request

import com.robert.models.OrderItem

data class OrderRequest(
    val userId: Int,
    val items: List<OrderItem>,
    val total: Double,
    val status: String,
)
