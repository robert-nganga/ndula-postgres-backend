package com.robert.models

import kotlinx.serialization.Serializable


@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val image: String,
    val password: String,
    val salt: String
)
