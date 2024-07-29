package com.robert.db.dao.wish_list

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.dao.shoe.ShoeDao
import com.robert.db.tables.wish_list.WishListItemsTable
import com.robert.db.tables.wish_list.WishListTable
import com.robert.models.WishList
import com.robert.models.WishListItem
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime

class WishListDaoImpl(private val shoeDao: ShoeDao) : WishListDao {

    override suspend fun getWishlistByUserId(userId: Int): WishList = dbQuery {
        val wishlistId = WishListTable.select { WishListTable.userId eq userId }
            .map { it[WishListTable.id] }
            .singleOrNull() ?: createWishlist(userId)

        val items = WishListItemsTable
            .select { WishListItemsTable.wishlistId eq wishlistId }
            .map { row ->
                val shoe = shoeDao.getShoeById(row[WishListItemsTable.shoeId])
                WishListItem(
                    id = row[WishListItemsTable.id],
                    shoe = shoe,
                    createdAt = row[WishListItemsTable.createdAt].toString()
                )
            }

        WishListTable.select { WishListTable.id eq wishlistId }
            .map { row ->
                WishList(
                    id = row[WishListTable.id],
                    items = items,
                    createdAt = row[WishListTable.createdAt].toString(),
                    updatedAt = row[WishListTable.updatedAt].toString()
                )
            }
            .single()
    }

    private suspend fun createWishlist(userId: Int): Int = dbQuery {
        WishListTable.insert {
            it[WishListTable.userId] = userId
        } get WishListTable.id
    }

    override suspend fun addItemToWishlist(userId: Int, shoeId: Int): WishList = dbQuery {
        val wishlistId = getWishlistByUserId(userId).id
        WishListItemsTable.insert {
            it[WishListItemsTable.wishlistId] = wishlistId
            it[WishListItemsTable.shoeId] = shoeId
        }
        setUpdatedAt(wishlistId)
        getWishlistByUserId(userId)
    }

    override suspend fun removeItemFromWishlist(userId: Int, shoeId: Int): WishList = dbQuery {
        val wishlistId = getWishlistByUserId(userId).id
        WishListItemsTable.deleteWhere {
            (WishListItemsTable.wishlistId eq wishlistId) and (WishListItemsTable.shoeId eq shoeId)
        }
        setUpdatedAt(wishlistId)
        getWishlistByUserId(userId)
    }

    override suspend fun clearWishlist(userId: Int): WishList = dbQuery {
        val wishlistId = getWishlistByUserId(userId).id
        WishListItemsTable.deleteWhere { WishListItemsTable.wishlistId eq wishlistId }
        setUpdatedAt(wishlistId)
        getWishlistByUserId(userId)
    }

    override suspend fun isShoeInWishlist(userId: Int, shoeId: Int): Boolean = dbQuery {
        val wishlistId = WishListTable
            .select { WishListTable.userId eq userId }
            .map { it[WishListTable.id] }
            .singleOrNull() ?: return@dbQuery false

        WishListItemsTable
            .select {
                (WishListItemsTable.wishlistId eq wishlistId) and
                        (WishListItemsTable.shoeId eq shoeId)
            }
            .count() > 0
    }

    private suspend fun setUpdatedAt(wishListId: Int) = dbQuery {
        WishListTable.update({ WishListTable.id eq wishListId }) {
            it[updatedAt] = CurrentDateTime
        }
    }
}