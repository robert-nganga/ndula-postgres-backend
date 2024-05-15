package com.robert.models

data class PaginatedShoes(
    val shoes: List<Shoe>,
    val totalCount: Int
)
