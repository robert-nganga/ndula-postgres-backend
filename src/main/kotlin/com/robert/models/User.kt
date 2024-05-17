package com.robert.models


data class User(
    val id: Int,
    val name: String,
    val email: String,
    val image: String,
    val cart: Cart,
    val createdAt: String,
    val password: String,
    val salt: String
)
