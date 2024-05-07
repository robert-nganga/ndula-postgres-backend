package com.robert.request

import kotlinx.serialization.Serializable


@Serializable
data class UserRequest (
    val name: String,
    val email: String,
    val password: String,
    val image: String
)
