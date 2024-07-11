package com.robert.response

import com.robert.models.Cart
import com.robert.models.User


data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val image: String,
    val token: String,
    val created: String,
    val cartId: Int
)



fun User.toUserResponse(token: String) = UserResponse(
    id = id,
    name = name,
    email = email,
    token = token,
    image = image,
    created = createdAt,
    cartId = cart.id
)
