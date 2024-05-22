package com.robert.routes

import com.robert.repositories.images.ImageRepository
import com.robert.utils.BaseResponse
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.imageRoutes(
    imageRepository: ImageRepository
) {
    post("/upload") {
        val multipartData = call.receiveMultipart()
        var imageBytes: ByteArray? = null
        var imageName: String? = null
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val fileBytes = part.streamProvider().readBytes()
                    imageName = part.originalFileName
                    imageBytes = fileBytes
                }
                // Handle other parts if needed
                else -> {}
            }
            part.dispose()
        }

        if (imageBytes != null && imageName != null) {
            val response = imageRepository.uploadImage(imageBytes!!, imageName!!)
            when (response) {
                is BaseResponse.SuccessResponse -> {
                    call.respond(response.status, mapOf("image_url" to response.data))
                }
                is BaseResponse.ErrorResponse -> {
                    call.respond(response.status, response)
                }
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Image not provided")
        }
    }
}