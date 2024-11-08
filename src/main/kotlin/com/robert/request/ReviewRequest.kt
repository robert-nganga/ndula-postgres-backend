package com.robert.request

import com.robert.models.Review

data class ReviewRequest (
    val userId: Int,
    val orderItemId: Int,
    val userName: String,
    val userImage: String,
    val shoeId: Int,
    val rating: Double,
    val comment: String,
)

fun ReviewRequest.toReview(): Review {
    return Review(
        userId = userId,
        userImage = userImage,
        userName = userName,
        shoeId = shoeId,
        rating = rating,
        comment = comment
    )
}