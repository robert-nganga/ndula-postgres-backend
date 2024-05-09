package com.robert.repositories

import com.robert.models.User
import com.robert.response.UserResponse
import com.robert.utils.BaseResponse

interface UserRepository {
    suspend fun createUser(user: User): BaseResponse<UserResponse>
    suspend fun loginUser(email: String, password: String): BaseResponse<UserResponse>
}