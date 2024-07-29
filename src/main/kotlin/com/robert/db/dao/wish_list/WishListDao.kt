package com.robert.db.dao.wish_list

import com.robert.models.WishList

interface WishListDao {
    suspend fun getWishlistByUserId(userId: Int): WishList
    suspend fun addItemToWishlist(userId: Int, shoeId: Int): WishList
    suspend fun removeItemFromWishlist(userId: Int, shoeId: Int): WishList
    suspend fun clearWishlist(userId: Int): WishList
    suspend fun isShoeInWishlist(userId: Int, shoeId: Int): Boolean
}