package com.robert.repositories.cart

import com.robert.models.Cart
import com.robert.utils.BaseResponse

interface CartRepository {
    suspend fun getCartById(cartId: Int): BaseResponse<Cart>
    suspend fun addItemToCart(cartId: Int, shoeId: Int, quantity: Int): BaseResponse<Cart>
    suspend fun updateCartItemQuantity(cartId: Int, cartItemId: Int, newQuantity: Int): BaseResponse<Cart>
    suspend fun removeItemFromCart(cartId: Int, cartItemId: Int): BaseResponse<Cart>
    suspend fun clearCart(cartId: Int): BaseResponse<Cart>
}