package com.robert.response

data class OutOfStockError(
    val message: String,
    val shoeId: Int,
    val variationId: Int
)
