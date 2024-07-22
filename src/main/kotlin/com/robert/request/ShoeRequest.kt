package com.robert.request

data class ShoeRequest(
    val name: String,
    val description: String?,
    val price: Double,
    val productType: String,
    val category: String,
    val brand: String?,
    val images:  List<String>,
    val variants: List<ShoeVariantRequest>,
)