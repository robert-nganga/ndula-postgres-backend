package com.robert.utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.pipeline.*

fun PipelineContext<Unit, ApplicationCall>.getUserIdFromAuthToken():Int? {
    val principal = call.principal<JWTPrincipal>()
    return principal?.getClaim("userId", String::class)?.toIntOrNull()
}