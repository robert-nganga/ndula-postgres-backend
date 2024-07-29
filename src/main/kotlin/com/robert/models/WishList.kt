package com.robert.models

data class WishList(
    val id: Int,
    val items: List<WishListItem>,
    val createdAt: String,
    val updatedAt: String
)
