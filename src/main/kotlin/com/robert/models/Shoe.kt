package com.robert.models



data class Shoe(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val productType: String,
    val category: String,
    val averageRating: Double,
    val totalReviews: Int,
    val brand: Brand?,
    val images:  List<String>,
    val variants: List<ShoeVariant>,
    val createdAt: String,
    val isInWishList: Boolean = false
)