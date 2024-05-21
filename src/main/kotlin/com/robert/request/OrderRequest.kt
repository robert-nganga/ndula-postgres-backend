package com.robert.request

data class OrderRequest(
    val userId: Int,
    val items: List<OrderItemRequest>,
    val total: Double,
    val status: String,
)
