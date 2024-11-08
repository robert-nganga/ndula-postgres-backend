package com.robert.models

data class PaginatedReview(
    val reviews: List<Review>,
    val totalCount: Int
)
