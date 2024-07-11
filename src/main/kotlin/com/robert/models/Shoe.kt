package com.robert.models



data class Shoe(
    val id: Int,
    val name: String,
    val description: String?,
    val category: String,
    val brand: String?,
    val images:  List<String>,
    val variants: List<ShoeVariant>,
    val createdAt: String
)