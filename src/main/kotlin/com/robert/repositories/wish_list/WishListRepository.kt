package com.robert.repositories.wish_list

import com.robert.models.WishList
import com.robert.utils.BaseResponse


interface WishListRepository {
    suspend fun getWishlistByUserId(userId: Int): BaseResponse<WishList>
    suspend fun addItemToWishlist(userId: Int, shoeId: Int): BaseResponse<WishList>
    suspend fun removeItemFromWishlist(userId: Int, shoeId: Int): BaseResponse<WishList>
    suspend fun clearWishlist(userId: Int): BaseResponse<WishList>
}