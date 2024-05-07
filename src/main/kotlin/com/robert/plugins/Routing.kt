package com.robert.plugins

import com.robert.db.dao.user.UserDao
import com.robert.repositories.UserRepository
import com.robert.repositories.UserRepositoryImpl
import com.robert.routes.authCheck
import com.robert.routes.login
import com.robert.routes.signUp
import com.robert.security.hashing.HashingService
import com.robert.security.tokens.TokenConfig
import com.robert.security.tokens.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    hashingService: HashingService,
    userRepository: UserRepository
) {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, mapOf("message" to "Healthy"))
        }
        authenticate {
            authCheck()
        }
        route("/auth") {
            login(
                userRepository = userRepository,
            )
            signUp(
                hashingService = hashingService,
                userRepository = userRepository
            )
        }
    }
}
