package com.robert

import com.robert.db.DatabaseFactory
import com.robert.db.dao.user.UserDaoImpl
import com.robert.plugins.*
import com.robert.repositories.UserRepositoryImpl
import com.robert.security.hashing.SHA256HashingService
import com.robert.security.tokens.JwtTokenService
import com.robert.security.tokens.TokenConfig
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    install(ContentNegotiation){
        jackson()
    }
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 7L * 1000L * 60L * 60L * 24L,
        secret = "my-secret"
    )
    val tokenService = JwtTokenService()
    val userDao = UserDaoImpl()
    val hashingService = SHA256HashingService()

    val userRepository = UserRepositoryImpl(
        userDao = userDao,
        tokenService = tokenService,
        tokenConfig = tokenConfig,
        hashingService = hashingService
    )
    //configureSerialization()
    //configureDatabases()
    configureMonitoring()
    configureSecurity(
        tokenConfig = tokenConfig,
    )
    configureRouting(
        userRepo = UserDaoImpl(),
        hashingService = hashingService,
        tokenConfig = tokenConfig,
        tokenService = tokenService,
        userRepository = userRepository
    )
}
