package com.robert.models

data class ShoeVariant(
    val id: Int,
    val image: String?,
    val price: Double,
    val size: Int,
    val color: String,
    val quantity: Int
)
