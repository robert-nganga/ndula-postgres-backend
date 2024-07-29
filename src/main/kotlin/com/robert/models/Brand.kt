package com.robert.models




data class Brand(
    val id: Int,
    val name: String,
    val description: String?,
    val logoUrl: String?,
    val shoes: Int = 0
)
