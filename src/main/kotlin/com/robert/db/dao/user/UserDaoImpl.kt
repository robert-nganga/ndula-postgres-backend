package com.robert.db.dao.user

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.dao.cart.CartDao
import com.robert.db.dao.shoe.ShoeDao
import com.robert.db.tables.cart.CartItemsTable
import com.robert.db.tables.cart.CartTable
import com.robert.db.tables.shoe.ShoesTable
import com.robert.db.tables.user.UsersTable
import com.robert.models.Cart
import com.robert.models.CartItem
import com.robert.models.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class UserDaoImpl(
    private val shoeDao: ShoeDao,
    private val cartDao: CartDao
): UserDao {

    private fun resultRowToNode(row: ResultRow): User {
        val userId = row[UsersTable.id]
        val cartId = row[UsersTable.cartId]

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

        val cart = CartTable
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

        return User(
            id = userId,
            name = row[UsersTable.name],
            email = row[UsersTable.email],
            image = row[UsersTable.image],
            cart = cart,
            createdAt = row[UsersTable.created].toString(),
            password = row[UsersTable.password],
            salt = row[UsersTable.salt]
        )
    }

    override suspend fun createUser(user: User): User? = dbQuery {
        val insertStatement = UsersTable.insert {
            it[name] = user.name
            it[password] = user.password
            it[email] = user.email
            it[image] = user.image
            it[salt] = user.salt
            it[cartId] = 0 //Placeholder value
        }
        val userId = insertStatement.resultedValues?.singleOrNull()?.get(UsersTable.id) ?: return@dbQuery null
        val cartId = cartDao.createCart(userId).id
        UsersTable.update({ UsersTable.id eq userId }) {
            it[UsersTable.cartId] = cartId
        }
        val userRow = UsersTable.select { UsersTable.id eq userId }.single()
        resultRowToNode(userRow)
    }


    override suspend fun findUserByEmail(email: String): User? = dbQuery {
        UsersTable
            .select{ UsersTable.email eq email }
            .map(::resultRowToNode)
            .singleOrNull()
    }
}