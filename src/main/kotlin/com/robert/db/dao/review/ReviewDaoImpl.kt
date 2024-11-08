package com.robert.db.dao.review

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.tables.order.OrderItemsTable
import com.robert.db.tables.review.ReviewsTable
import com.robert.db.tables.shoe.ShoesTable
import com.robert.models.PaginatedReview
import com.robert.models.Review
import com.robert.models.ReviewFilterOptions
import com.robert.request.ReviewRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class ReviewDaoImpl: ReviewDao {

    private fun resultToRow(row: ResultRow): Review{
        return Review(
            id = row[ReviewsTable.id],
            userId = row[ReviewsTable.userId],
            userName = row[ReviewsTable.userName],
            userImage = row[ReviewsTable.userImage],
            shoeId = row[ReviewsTable.shoeId],
            rating = row[ReviewsTable.rating].toDouble(),
            comment = row[ReviewsTable.comment]
        )
    }

    override suspend fun addReview(review: ReviewRequest): Review = dbQuery {
        val reviewId = ReviewsTable.insert {
            it[userId] = review.userId
            it[shoeId] = review.shoeId
            it[rating] = review.rating.toBigDecimal()
            it[comment] = review.comment
            it[userName] = review.userName
            it[userImage] = review.userImage
        } get ReviewsTable.id

        // Update average rating for this shoe
        updateShoeRating(review.shoeId)

        // Update order items table
        updateOrderItem(orderItemId = review.orderItemId, rating = review.rating)

        ReviewsTable
            .select { ReviewsTable.id eq reviewId }
            .map(::resultToRow)
            .single()
    }

    override suspend fun getReviewsForShoe(filterOptions: ReviewFilterOptions, shoeId: Int): PaginatedReview = dbQuery {
        val offset = (filterOptions.page - 1) * filterOptions.pageSize
        val totalCount = ReviewsTable.select{
            ReviewsTable.shoeId eq shoeId
        }.count().toInt()

        val query = ReviewsTable.select { ReviewsTable.shoeId eq shoeId }

        filterOptions.rating?.let { rating ->
            query.andWhere {
                (ReviewsTable.rating greaterEq  rating.toBigDecimal()) and
                        (ReviewsTable.rating less  (rating + 1).toBigDecimal())
            }
        }

        val reviews = query
            .orderBy(ReviewsTable.rating to SortOrder.DESC, ReviewsTable.createdAt to SortOrder.DESC)
            .limit(filterOptions.pageSize, offset.toLong())
            .map(::resultToRow)

        PaginatedReview(
            totalCount = totalCount,
            reviews = reviews
        )
    }

    override suspend fun getFeaturedReviewsForShoe(shoeId: Int): List<Review> = dbQuery {
        ReviewsTable
            .select { ReviewsTable.shoeId eq shoeId }
            .orderBy(ReviewsTable.rating to SortOrder.DESC, ReviewsTable.createdAt to SortOrder.DESC)
            .limit(3)
            .map(::resultToRow)
    }

    override suspend fun filterReviewsByRating(rating: Double, shoeId: Int): List<Review> = dbQuery {
        ReviewsTable
            .select { ReviewsTable.shoeId eq shoeId and(ReviewsTable.rating eq rating.toBigDecimal()) }
            .map(::resultToRow)
    }

    override suspend fun getAverageReviewsForShoe(shoeId: Int): Double = dbQuery {
        ReviewsTable
            .select { ReviewsTable.shoeId eq shoeId }
            .map{ it[ReviewsTable.rating].toDouble() }
            .average()
    }

    override suspend fun deleteReview(reviewId: Int): Boolean = dbQuery {
        val review = ReviewsTable.select { ReviewsTable.id eq reviewId }.singleOrNull()
        val result = ReviewsTable.deleteWhere { id eq reviewId } > 0
        if (result && review != null) {
            updateShoeRating(review[ReviewsTable.shoeId])
        }
        result
    }

    private fun updateShoeRating(shoeId: Int) {
        val reviews = ReviewsTable.select { ReviewsTable.shoeId eq shoeId }.map(::resultToRow)
        val totalReviews = reviews.count()
        val averageRating = if (totalReviews > 0) {
            reviews.map { it.rating }.average()
        } else {
            0.0
        }

        ShoesTable.update({ ShoesTable.id eq shoeId }) {
            it[ShoesTable.averageRating] = averageRating.toBigDecimal()
            it[ShoesTable.totalReviews] = totalReviews
        }
    }

    private fun updateOrderItem(orderItemId: Int, rating: Double) {
        OrderItemsTable
            .update ({ OrderItemsTable.id eq orderItemId }){
                it[OrderItemsTable.rating] = rating.toBigDecimal()
            }
    }
}