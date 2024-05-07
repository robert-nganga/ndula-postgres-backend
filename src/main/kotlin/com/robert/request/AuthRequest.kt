package com.robert.request

import kotlinx.serialization.Serializable


@Serializable
data class AuthRequest(
    val password: String,
    val email: String
)
