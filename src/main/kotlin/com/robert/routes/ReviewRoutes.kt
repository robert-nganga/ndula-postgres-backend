package com.robert.routes

import com.robert.models.ReviewFilterOptions
import com.robert.repositories.reviews.ReviewRepository
import com.robert.request.ReviewRequest
import com.robert.utils.BaseResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.reviewRoutes(reviewRepository: ReviewRepository) {

    post("/add") {
        val review = call.receive<ReviewRequest>()
        when(val result = reviewRepository.addReview(review)){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    get("/shoe/{shoeId}") {
        val filterOptions = ReviewFilterOptions(
            page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1,
            pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 15,
            rating = call.request.queryParameters["rating"]?.toDoubleOrNull()
        )

        val shoeId = call.parameters["shoeId"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
        when(val result = reviewRepository.getReviewsForShoe(filterOptions, shoeId)){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    get("/shoe/featured/{shoeId}") {
        val shoeId = call.parameters["shoeId"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
        when(val result = reviewRepository.getFeaturedReviewsForShoe(shoeId)){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }

    delete("/{reviewId}") {
        val reviewId = call.parameters["reviewId"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
        when(val result = reviewRepository.deleteReview(reviewId)){
            is BaseResponse.SuccessResponse -> call.respond(result.status, result.data!!)
            is BaseResponse.ErrorResponse -> call.respond(result.status, result)
        }
    }
}