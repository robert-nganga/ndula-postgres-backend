package com.robert.models


data class ShoeFilterOptions(
    val category: String?,
    val brand: String?,
    val minPrice: Double?,
    val maxPrice: Double?,
    val sortBy: String,
    val sortOrder: String,
    val page: Int,
    val pageSize: Int
)