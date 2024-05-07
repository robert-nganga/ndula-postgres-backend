package com.robert.plugins

import com.robert.db.dao.user.UserDao
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
    userRepo: UserDao,
    hashingService: HashingService,
    tokenConfig: TokenConfig,
    tokenService: TokenService
) {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, mapOf("message" to "Healthy"))
        }
        authenticate {
            authCheck()
        }

        login(
            userRepo = userRepo,
            hashingService = hashingService,
            tokenService = tokenService,
            tokenConfig = tokenConfig,
        )
        signUp(
            userRepo = userRepo,
            hashingService = hashingService,
            tokenService = tokenService,
            tokenConfig = tokenConfig,
        )
    }
}
