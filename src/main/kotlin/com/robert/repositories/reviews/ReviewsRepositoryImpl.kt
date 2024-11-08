package com.robert.repositories.reviews

import com.robert.db.dao.review.ReviewDao
import com.robert.models.PaginatedReview
import com.robert.models.Review
import com.robert.models.ReviewFilterOptions
import com.robert.request.ReviewRequest
import com.robert.utils.BaseResponse
import io.ktor.http.HttpStatusCode

class ReviewRepositoryImpl(private val reviewDao: ReviewDao) : ReviewRepository {

    override suspend fun addReview(review: ReviewRequest): BaseResponse<Review> {
        return try {
            val result = reviewDao.addReview(review)
            BaseResponse.SuccessResponse(data = result, status = HttpStatusCode.Created)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(message = "Error adding review: ${e.message}", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getReviewsForShoe(filterOptions: ReviewFilterOptions, shoeId: Int): BaseResponse<PaginatedReview> {
        return try {
            val reviews = reviewDao.getReviewsForShoe(filterOptions, shoeId)
            BaseResponse.SuccessResponse(data = reviews)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(message = "Error fetching reviews: ${e.message}", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getFeaturedReviewsForShoe(shoeId: Int): BaseResponse<List<Review>> {
        return try {
            val reviews = reviewDao.getFeaturedReviewsForShoe(shoeId)
            BaseResponse.SuccessResponse(data = reviews)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(message = "Error fetching reviews: ${e.message}", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun filterReviewsByRating(rating: Double, shoeId: Int): BaseResponse<List<Review>> {
        return try {
            val reviews = reviewDao.filterReviewsByRating(rating, shoeId)
            BaseResponse.SuccessResponse(data = reviews)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(message = "Error fetching reviews: ${e.message}", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getAverageReviewsForShoe(shoeId: Int): BaseResponse<Double> {
        return try {
            val averageRating = reviewDao.getAverageReviewsForShoe(shoeId)
            BaseResponse.SuccessResponse(data = averageRating)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(message = "Error calculating average rating: ${e.message}", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun deleteReview(reviewId: Int): BaseResponse<Boolean> {
        return try {
            val result = reviewDao.deleteReview(reviewId)
            if (result) {
                BaseResponse.SuccessResponse(data = true, message = "Review successfully deleted")
            } else {
                BaseResponse.ErrorResponse(message = "Failed to delete review", status = HttpStatusCode.NotFound)
            }
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(message = "Error deleting review: ${e.message}", status = HttpStatusCode.InternalServerError)
        }
    }
}