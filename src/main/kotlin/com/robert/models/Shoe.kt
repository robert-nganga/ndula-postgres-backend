package com.robert.models



data class Shoe(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val category: String,
    val images:  List<String>,
    val sizes: List<ShoeSize>,
    val createdAt: String
)