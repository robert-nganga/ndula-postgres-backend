package com.robert

import com.robert.db.DatabaseFactory
import com.robert.db.dao.user.UserDaoImpl
import com.robert.plugins.*
import com.robert.security.hashing.SHA256HashingService
import com.robert.security.tokens.JwtTokenService
import com.robert.security.tokens.TokenConfig
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 7L * 1000L * 60L * 60L * 24L,
        secret = "my-secret"
    )
    configureSerialization()
    //configureDatabases()
    configureMonitoring()
    configureSecurity()
    configureRouting(
        userRepo = UserDaoImpl(),
        hashingService = SHA256HashingService(),
        tokenConfig = tokenConfig,
        tokenService = JwtTokenService()
    )
}
