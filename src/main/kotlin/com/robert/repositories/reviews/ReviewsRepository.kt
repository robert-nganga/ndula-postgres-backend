package com.robert.repositories.reviews

import com.robert.models.PaginatedReview
import com.robert.models.Review
import com.robert.models.ReviewFilterOptions
import com.robert.request.ReviewRequest
import com.robert.utils.BaseResponse

interface ReviewRepository {
    suspend fun addReview(review: ReviewRequest): BaseResponse<Review>
    suspend fun getReviewsForShoe(filterOptions: ReviewFilterOptions, shoeId: Int): BaseResponse<PaginatedReview>
    suspend fun getFeaturedReviewsForShoe(shoeId: Int): BaseResponse<List<Review>>
    suspend fun filterReviewsByRating(rating: Double, shoeId: Int): BaseResponse<List<Review>>
    suspend fun getAverageReviewsForShoe(shoeId: Int): BaseResponse<Double>
    suspend fun deleteReview(reviewId: Int): BaseResponse<Boolean>
}