package com.robert.db.dao.review

import com.robert.models.PaginatedReview
import com.robert.models.Review
import com.robert.models.ReviewFilterOptions
import com.robert.request.ReviewRequest

interface ReviewDao {
    suspend fun addReview(review: ReviewRequest): Review
    suspend fun getReviewsForShoe(filterOptions: ReviewFilterOptions, shoeId: Int): PaginatedReview
    suspend fun getFeaturedReviewsForShoe(shoeId: Int): List<Review>
    suspend fun filterReviewsByRating(rating: Double, shoeId: Int): List<Review>
    suspend fun getAverageReviewsForShoe(shoeId: Int): Double
    suspend fun deleteReview(reviewId: Int): Boolean
}