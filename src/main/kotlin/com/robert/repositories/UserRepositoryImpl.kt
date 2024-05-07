package com.robert.repositories

import com.robert.db.dao.user.UserDao
import com.robert.models.User
import com.robert.response.toUserResponse
import com.robert.security.hashing.HashingService
import com.robert.security.hashing.SaltedHash
import com.robert.security.tokens.JwtTokenService
import com.robert.security.tokens.TokenClaim
import com.robert.security.tokens.TokenConfig
import com.robert.utils.BaseResponse
import io.ktor.http.*

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val tokenService: JwtTokenService,
    private val tokenConfig: TokenConfig,
    private val hashingService: HashingService,
): UserRepository {

    override suspend fun createUser(user: User): BaseResponse<Any> {
        return  if(doesUserExist(user.email)) {
            BaseResponse.ErrorResponse(message = "Email already registered", status = HttpStatusCode.Conflict)
        } else {
            val createdUser = userDao.createUser(user)
            if (createdUser  != null) {
                val token = tokenService.generate(
                    config = tokenConfig,
                    TokenClaim(
                        name = "userId",
                        value = createdUser.id.toString()
                    )
                )
                BaseResponse.SuccessResponse(data =  user.toUserResponse(token))
            } else {
                BaseResponse.ErrorResponse(message = "Error creating user")
            }
        }
    }

    override suspend fun loginUser(email: String, password: String): BaseResponse<Any> {
        val user = userDao.findUserByEmail(email)
        return if (user != null) {
            val token = tokenService.generate(
                config = tokenConfig,
                TokenClaim(
                    name = "userId",
                    value = user.id.toString()
                )
            )
            val isValidPassword = hashingService.verify(
                value = password,
                saltedHash = SaltedHash(
                    hash = user.password,
                    salt = user.salt
                )
            )
            if (isValidPassword) {
                BaseResponse.SuccessResponse(data = user.toUserResponse(token))
            } else {
                BaseResponse.ErrorResponse(message = "Invalid email or password", status = HttpStatusCode.Forbidden)
            }
        } else {
            BaseResponse.ErrorResponse(message = "Invalid email or password",  status = HttpStatusCode.Forbidden)
        }
    }

    private suspend fun doesUserExist(email: String): Boolean = userDao.findUserByEmail(email) != null

}