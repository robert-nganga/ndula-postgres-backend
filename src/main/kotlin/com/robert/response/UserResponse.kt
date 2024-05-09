package com.robert.response

import com.robert.models.User
import kotlinx.serialization.Serializable


@Serializable
data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val image: String,
    val token: String,
    val created: Long
)



fun User.toUserResponse(token: String) = UserResponse(
    id = id,
    name = name,
    email = email,
    token = token,
    image = image,
    created = createdAt
)
