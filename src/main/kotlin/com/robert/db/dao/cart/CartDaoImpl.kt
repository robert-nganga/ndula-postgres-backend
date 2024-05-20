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
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class CartDaoImpl(
    private val shoeDao: ShoeDao
) : CartDao {
    override suspend fun getCartById(cartId: Int): Cart = dbQuery {
        val existingCartItems = CartItemsTable
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

        val cart = CartTable
            .select { CartTable.id eq cartId }
            .map { resultRow ->
                Cart(
                    id = resultRow[CartTable.id],
                    items = existingCartItems,
                    createdAt = resultRow[CartTable.createdAt].toString(),
                    updatedAt = resultRow[CartTable.updatedAt].toString()
                )
            }
            .single()

        cart
    }

    override suspend fun createCart(): Cart = dbQuery {
        val id = CartTable.insert {} get CartTable.id
        getCartById(id)
    }

    override suspend fun addItemToCart(cartId: Int, shoeId: Int, quantity: Int): Cart = dbQuery {
        val insertStatement = CartItemsTable.insert {
            it[CartItemsTable.cartId] = cartId
            it[CartItemsTable.shoeId] = shoeId
            it[CartItemsTable.quantity] = quantity
        }
        val insertedItem = insertStatement.resultedValues?.single()?.let { resultRow ->
            val shoe = ShoesTable
                .select { ShoesTable.id eq resultRow[CartItemsTable.shoeId] }
                .map { shoeDao.resultRowToShoe(it) }
                .single()
            CartItem(
                id = resultRow[CartItemsTable.id],
                shoe = shoe,
                quantity = resultRow[CartItemsTable.quantity]
            )
        } ?: throw Exception("Insert failed")
        updateCart(cartId)
        val cart = getCartById(cartId)
        cart.copy(
            items = if (!cart.items.contains(insertedItem)) cart.items + insertedItem else cart.items
        )
    }

    override suspend fun updateCartItemQuantity(cartId: Int, cartItemId: Int, newQuantity: Int): Cart = dbQuery {
        CartItemsTable.update({ CartItemsTable.id eq cartItemId }) {
            it[quantity] = newQuantity
        }
        updateCart(cartId)
        val cart = getCartById(cartId)
        cart.copy(
            items = cart.items.map { item ->
                if (item.id == cartItemId) {
                    item.copy(quantity = newQuantity)
                } else {
                    item
                }
            }
        )
    }

    override suspend fun removeItemFromCart(cartId: Int, cartItemId: Int): Cart = dbQuery {
        CartItemsTable.deleteWhere { id eq cartItemId }
        updateCart(cartId)
        val cart = getCartById(cartId)
        cart.copy(
            items = cart.items.filter { it.id != cartItemId }
        )
    }

    override suspend fun clearCart(cartId: Int): Cart = dbQuery {
        CartItemsTable.deleteWhere { CartItemsTable.cartId eq cartId }
        updateCart(cartId)
        val cart = getCartById(cartId)
        cart.copy(
            items = emptyList()
        )
    }

    private suspend fun updateCart(cartId: Int) = dbQuery {
        CartTable.update({ CartTable.id eq cartId }) {
            it[updatedAt] = CurrentDateTime
        }
    }

}