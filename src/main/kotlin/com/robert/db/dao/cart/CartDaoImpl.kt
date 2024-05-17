package com.robert.db.dao.cart

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.dao.shoe.ShoeDao
import com.robert.db.tables.cart.CartItemsTable
import com.robert.db.tables.cart.CartTable
import com.robert.db.tables.shoe.ShoesTable
import com.robert.models.Cart
import com.robert.models.CartItem
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class CartDaoImpl(
    private val shoeDao: ShoeDao
) : CartDao {
    override suspend fun getCartById(cartId: Int): Cart = dbQuery {
        val cartItems = CartItemsTable
            .select { CartItemsTable.cartId eq cartId }
            .map { resultRow ->
                val shoe = ShoesTable
                    .select { ShoesTable.id eq resultRow[CartItemsTable.shoeId] }
                    .map { shoeDao.resultRowToShoe(it) }
                    .single()
                CartItem(
                    id = resultRow[CartItemsTable.id],
                    shoe = shoe,
                    quantity = resultRow[CartItemsTable.quantity]
                )
            }

        CartTable
            .select { CartTable.id eq cartId }
            .map { resultRow ->
                Cart(
                    id = resultRow[CartTable.id],
                    items = cartItems,
                    createdAt = resultRow[CartTable.createdAt].toString(),
                    updatedAt = resultRow[CartTable.updatedAt].toString()
                )
            }
            .single()
    }

    override suspend fun createCart(userId: Int): Cart = dbQuery {
        val id = CartTable.insert {} get CartTable.id
        getCartById(id)
    }

    override suspend fun addItemToCart(cartId: Int, shoeId: Int, quantity: Int): Cart = dbQuery {
        CartItemsTable.insert {
            it[CartItemsTable.cartId] = cartId
            it[CartItemsTable.shoeId] = shoeId
            it[CartItemsTable.quantity] = quantity
        }
        getCartById(cartId)
    }

    override suspend fun updateCartItemQuantity(cartId: Int, cartItemId: Int, newQuantity: Int): Cart = dbQuery {
        CartItemsTable.update({ CartItemsTable.id eq cartItemId }) {
            it[quantity] = newQuantity
        }
        getCartById(cartItemId)
    }

    override suspend fun removeItemFromCart(cartId: Int, cartItemId: Int): Cart = dbQuery {
        CartItemsTable.deleteWhere { id eq cartItemId }
        getCartById(cartId)
    }

    override suspend fun clearCart(cartId: Int): Cart = dbQuery {
        CartItemsTable.deleteWhere { CartItemsTable.cartId eq cartId }
        getCartById(cartId)
    }

}