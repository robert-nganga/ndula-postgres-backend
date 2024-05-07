package com.robert.db.dao.user

import com.robert.models.User

interface UserDao {
    suspend fun createUser(user: User): User?
    suspend fun findUserByEmail(email: String): User?
}