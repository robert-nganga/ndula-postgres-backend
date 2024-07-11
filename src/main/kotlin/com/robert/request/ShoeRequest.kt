package com.robert.request

import com.robert.models.Shoe
import com.robert.models.ShoeVariant

data class ShoeRequest(
    val name: String,
    val description: String?,
    val price: Double,
    val category: String,
    val brand: String?,
    val images:  List<String>,
    val variants: List<ShoeVariant>,
)

fun ShoeRequest.toShoe() = Shoe(
    id = 0,
    name = name,
    description = description,
    category = category,
    brand = brand,
    images = images,
    variants = variants,
    createdAt = "",
)
