package com.robert.request

import com.robert.models.ShoeVariant

data class ShoeVariantRequest(
    val price: Double,
    val size: Int,
    val color: String,
    val quantity: Int,
    val image: String?
)

fun ShoeVariantRequest.toShoeVariant(): ShoeVariant = ShoeVariant(
    id = 0,
    price = price,
    size = size,
    color = color,
    quantity = quantity,
    image = image
)
