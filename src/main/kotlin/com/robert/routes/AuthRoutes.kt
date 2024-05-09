package com.robert.routes

import com.robert.models.User
import com.robert.repositories.UserRepository
import com.robert.request.AuthRequest
import com.robert.request.UserRequest
import com.robert.response.ErrorResponse
import com.robert.security.hashing.HashingService
import com.robert.utils.BaseResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.login(
    userRepository: UserRepository
) {
    post("/login") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@post
        }
        val result = userRepository.loginUser(request.email, request.password)
        when(result){
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
        }
        return@post
    }
}

fun Route.signUp(
    hashingService: HashingService,
    userRepository: UserRepository
    ) {
    post("/register") {
        val request = call.receiveNullable<UserRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@post
        }
        val saltedHash = hashingService.generateSaltedHash(request.password)
        val new = User(
            email = request.email,
            password = saltedHash.hash,
            salt = saltedHash.salt,
            name = request.name,
            image = request.image,
            id = 0,
            createdAt = System.currentTimeMillis(),
        )
        val result = userRepository.createUser(new)
        when(result){
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
        }
        return@post
    }
}