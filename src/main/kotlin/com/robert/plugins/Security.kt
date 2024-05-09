package com.robert.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.robert.security.tokens.TokenConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity(tokenConfig: TokenConfig) {
    authentication {
        jwt {
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(tokenConfig.secret))
                    .withAudience(tokenConfig.audience)
                    .withIssuer(tokenConfig.issuer)
                    .build()
            )
            validate { credential ->
                if (!credential.payload.getClaim("userId").asString().isNullOrEmpty()) {
                    JWTPrincipal(credential.payload)
                } else null
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Token is not valid or has expired"))
            }
        }
    }
}
