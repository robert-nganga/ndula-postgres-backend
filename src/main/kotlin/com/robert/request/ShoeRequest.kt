package com.robert.request

import com.robert.models.Shoe
import com.robert.models.ShoeSize

data class ShoeRequest(
    val name: String,
    val description: String?,
    val price: Double,
    val category: String,
    val brand: String?,
    val images:  List<String>,
    val sizes: List<ShoeSize>,
)

fun ShoeRequest.toShoe() = Shoe(
    id = 0,
    name = name,
    description = description,
    price = price,
    category = category,
    brand = brand,
    images = images,
    sizes = sizes,
    createdAt = "",
)
