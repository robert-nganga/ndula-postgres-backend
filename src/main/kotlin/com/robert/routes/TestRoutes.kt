package com.robert.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.authCheck() {
    get("/authCheck") {
        call.respond(HttpStatusCode.OK, mapOf("message" to "You are authenticated"))
    }
}