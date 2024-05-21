package com.robert.request

data class UpdateOrderStatusRequest(
    val orderId: Int,
    val status: String,
)
