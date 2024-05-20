package com.robert.repositories.cart

import com.robert.db.dao.cart.CartDao
import com.robert.models.Cart
import com.robert.utils.BaseResponse
import io.ktor.http.*

class CartRepositoryImpl(
    private val cartDao: CartDao
) : CartRepository {

    override suspend fun getCartById(cartId: Int): BaseResponse<Cart> {
        return try {
            val cart = cartDao.getCartById(cartId)
            BaseResponse.SuccessResponse(data = cart)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun addItemToCart(cartId: Int, shoeId: Int, quantity: Int): BaseResponse<Cart> {
        return try {
            val cart = cartDao.addItemToCart(cartId, shoeId, quantity)
            BaseResponse.SuccessResponse(data = cart, status = HttpStatusCode.Created)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun updateCartItemQuantity(cartId: Int, cartItemId: Int, newQuantity: Int): BaseResponse<Cart> {
        return try {
            val cart = cartDao.updateCartItemQuantity(cartId, cartItemId, newQuantity)
            BaseResponse.SuccessResponse(data = cart)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun removeItemFromCart(cartId: Int, cartItemId: Int): BaseResponse<Cart> {
        return try {
            val cart = cartDao.removeItemFromCart(cartId, cartItemId)
            BaseResponse.SuccessResponse(data = cart)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun clearCart(cartId: Int): BaseResponse<Cart> {
        return try {
            val cart = cartDao.clearCart(cartId)
            BaseResponse.SuccessResponse(data = cart)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }
}