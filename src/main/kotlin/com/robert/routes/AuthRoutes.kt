package com.robert.routes

import com.robert.db.dao.user.UserDao
import com.robert.models.User
import com.robert.request.AuthRequest
import com.robert.request.UserRequest
import com.robert.response.ErrorResponse
import com.robert.response.toUserResponse
import com.robert.security.hashing.HashingService
import com.robert.security.hashing.SaltedHash
import com.robert.security.tokens.TokenClaim
import com.robert.security.tokens.TokenConfig
import com.robert.security.tokens.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.login(
    userRepo: UserDao,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("/login") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@post
        }
        val user = userRepo.findByEmail(request.email)
        if (user == null){
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("Invalid email or password", "", HttpStatusCode.Conflict.value)
            )
            return@post
        }
        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword){
            call.respond(
                HttpStatusCode.Forbidden,
                ErrorResponse("Invalid email or password", "", HttpStatusCode.Forbidden.value)
            )
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )
        call.respond(HttpStatusCode.OK, user.toUserResponse(token))
    }
}

fun Route.signUp(
    userRepo: UserDao,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
    ) {
    post("/signup") {
        val request = call.receiveNullable<UserRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request", "", HttpStatusCode.BadRequest.value)
            )
            return@post
        }
        val user = userRepo.findByEmail(request.email)
        if (user != null){
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("User already exists", "", HttpStatusCode.Conflict.value)
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
        val createdUser = userRepo.createUser(new)
        if (createdUser == null){
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("User already exists", "", HttpStatusCode.Conflict.value)
            )
            return@post
        }
        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = createdUser.id.toString()
            )
        )
        call.respond(HttpStatusCode.OK, createdUser.toUserResponse(token))


    }
}