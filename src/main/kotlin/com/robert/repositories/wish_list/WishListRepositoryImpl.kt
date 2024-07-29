package com.robert.repositories.wish_list

import com.robert.db.dao.wish_list.WishListDao
import com.robert.models.WishList
import com.robert.utils.BaseResponse
import io.ktor.http.*

class WishListRepositoryImpl(private val wishlistDao: WishListDao) : WishListRepository {
    override suspend fun getWishlistByUserId(userId: Int): BaseResponse<WishList> {
        return try {
            val wishlist = wishlistDao.getWishlistByUserId(userId)
            BaseResponse.SuccessResponse(data = wishlist)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun addItemToWishlist(userId: Int, shoeId: Int): BaseResponse<WishList> {
        return try {
            val isItemInWishList = wishlistDao.isShoeInWishlist(userId, shoeId)
            if (isItemInWishList){
                BaseResponse.ErrorResponse("Item already exists in wishlist", HttpStatusCode.Conflict)
            } else {
                val wishlist = wishlistDao.addItemToWishlist(userId, shoeId)
                BaseResponse.SuccessResponse(data = wishlist, status = HttpStatusCode.Created)
            }

        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun removeItemFromWishlist(userId: Int, shoeId: Int): BaseResponse<WishList> {
        return try {
            val wishlist = wishlistDao.removeItemFromWishlist(userId, shoeId)
            BaseResponse.SuccessResponse(data = wishlist)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun clearWishlist(userId: Int): BaseResponse<WishList> {
        return try {
            val wishlist = wishlistDao.clearWishlist(userId)
            BaseResponse.SuccessResponse(data = wishlist)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }
}