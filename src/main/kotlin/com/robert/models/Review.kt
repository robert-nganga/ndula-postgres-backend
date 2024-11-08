package com.robert.models


data class Review(
    val id: Int = 0,
    val userId: Int,
    val userName: String,
    val userImage: String,
    val shoeId: Int,
    val rating: Double,
    val comment: String,
    val createdAt: String = ""
)
