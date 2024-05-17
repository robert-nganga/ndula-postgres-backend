package com.robert.db.dao.cart

import com.robert.models.Cart
import com.robert.models.CartItem

interface CartDao {
    suspend fun getCartById(cartId: Int): Cart
    suspend fun createCart(): Cart
    suspend fun addItemToCart(cartId: Int, shoeId: Int, quantity: Int): Cart
    suspend fun updateCartItemQuantity(cartId: Int, cartItemId: Int, newQuantity: Int): Cart
    suspend fun removeItemFromCart(cartId: Int, cartItemId: Int): Cart
    suspend fun clearCart(cartId: Int): Cart
}
