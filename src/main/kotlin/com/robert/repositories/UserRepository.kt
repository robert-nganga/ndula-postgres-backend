package com.robert.repositories

import com.robert.models.User
import com.robert.utils.BaseResponse

interface UserRepository {
    suspend fun createUser(user: User): BaseResponse<Any>
    suspend fun loginUser(email: String, password: String): BaseResponse<Any>
}