package com.robert.db.dao.wish_list

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.tables.shoe.*
import com.robert.db.tables.wish_list.WishListItemsTable
import com.robert.db.tables.wish_list.WishListTable
import com.robert.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime

class WishListDaoImpl : WishListDao {

    override suspend fun getWishlistByUserId(userId: Int): WishList = dbQuery {
        val wishlistId = WishListTable.select { WishListTable.userId eq userId }
            .map { it[WishListTable.id] }
            .singleOrNull() ?: createWishlist(userId)

        val items = WishListItemsTable
            .select { WishListItemsTable.wishlistId eq wishlistId }
            .map { row ->
                WishListItem(
                    id = row[WishListItemsTable.id],
                    shoe = ShoesTable.select { ShoesTable.id eq row[WishListItemsTable.shoeId] }
                        .map(::resultRowToShoe)
                        .single(),
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
        // Get or create wishlist in a single operation
        val wishlistId = WishListTable
            .slice(WishListTable.id)
            .select { WishListTable.userId eq userId }
            .singleOrNull()?.get(WishListTable.id) ?: (WishListTable.insert {
            it[WishListTable.userId] = userId
        } get WishListTable.id)

        // Insert new item and update timestamp in a single transaction
        WishListItemsTable.insert {
            it[WishListItemsTable.wishlistId] = wishlistId
            it[WishListItemsTable.shoeId] = shoeId
        }

        WishListTable.update({ WishListTable.id eq wishlistId }) {
            it[updatedAt] = CurrentDateTime
        }

        // Fetch the updated wishlist
        val items = WishListItemsTable
            .select { WishListItemsTable.wishlistId eq wishlistId }
            .map { row ->
                WishListItem(
                    id = row[WishListItemsTable.id],
                    shoe = ShoesTable.select { ShoesTable.id eq row[WishListItemsTable.shoeId] }
                        .map(::resultRowToShoe)
                        .single(),
                    createdAt = row[WishListItemsTable.createdAt].toString()
                )
            }

        WishList(
            id = wishlistId,
            items = items,
            createdAt = WishListTable.slice(WishListTable.createdAt)
                .select { WishListTable.id eq wishlistId }
                .single()[WishListTable.createdAt].toString(),
            updatedAt = WishListTable.slice(WishListTable.updatedAt)
                .select { WishListTable.id eq wishlistId }
                .single()[WishListTable.updatedAt].toString(),
        )
    }

    override suspend fun removeItemFromWishlist(userId: Int, shoeId: Int): WishList = dbQuery {
        val wishlistId = WishListTable.select { WishListTable.userId eq userId }
            .map { it[WishListTable.id] }
            .singleOrNull() ?: createWishlist(userId)

        WishListItemsTable.deleteWhere {
            (WishListItemsTable.wishlistId eq wishlistId) and (WishListItemsTable.shoeId eq shoeId)
        }
        WishListTable.update({ WishListTable.id eq wishlistId }) {
            it[updatedAt] = CurrentDateTime
        }
        // Fetch the updated wishlist
        val items = WishListItemsTable
            .select { WishListItemsTable.wishlistId eq wishlistId }
            .map { row ->
                WishListItem(
                    id = row[WishListItemsTable.id],
                    shoe = ShoesTable.select { ShoesTable.id eq row[WishListItemsTable.shoeId] }
                        .map(::resultRowToShoe)
                        .single(),
                    createdAt = row[WishListItemsTable.createdAt].toString()
                )
            }
        WishList(
            id = wishlistId,
            items = items,
            createdAt = WishListTable.slice(WishListTable.createdAt)
                .select { WishListTable.id eq wishlistId }
                .single()[WishListTable.createdAt].toString(),
            updatedAt = WishListTable.slice(WishListTable.updatedAt)
                .select { WishListTable.id eq wishlistId }
                .single()[WishListTable.updatedAt].toString(),
        )
    }

    override suspend fun clearWishlist(userId: Int): WishList = dbQuery {
        val wishlistId = WishListTable.select { WishListTable.userId eq userId }
            .map { it[WishListTable.id] }
            .singleOrNull() ?: createWishlist(userId)
        WishListItemsTable.deleteWhere { WishListItemsTable.wishlistId eq wishlistId }
        WishListTable.update({ WishListTable.id eq wishlistId }) {
            it[updatedAt] = CurrentDateTime
        }
        // Fetch the updated wishlist
        val items = WishListItemsTable
            .select { WishListItemsTable.wishlistId eq wishlistId }
            .map { row ->
                WishListItem(
                    id = row[WishListItemsTable.id],
                    shoe = ShoesTable.select { ShoesTable.id eq row[WishListItemsTable.shoeId] }
                        .map(::resultRowToShoe)
                        .single(),
                    createdAt = row[WishListItemsTable.createdAt].toString()
                )
            }
        WishList(
            id = wishlistId,
            items = items,
            createdAt = WishListTable.slice(WishListTable.createdAt)
                .select { WishListTable.id eq wishlistId }
                .single()[WishListTable.createdAt].toString(),
            updatedAt = WishListTable.slice(WishListTable.updatedAt)
                .select { WishListTable.id eq wishlistId }
                .single()[WishListTable.updatedAt].toString(),
        )
    }

    override suspend fun isShoeInWishlist(userId: Int?, shoeId: Int): Boolean = dbQuery {
        if (userId == null) return@dbQuery false
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


    private fun resultRowToShoe(row: ResultRow): Shoe {
        val images = ShoeImagesTable
            .select{ ShoeImagesTable.productId eq row[ShoesTable.id] }
            .map{ it[ShoeImagesTable.imageUrl] }

        val variants = ShoeVariationsTable
            .select{ ShoeVariationsTable.productId eq row[ShoesTable.id] }
            .map {
                ShoeVariant(
                    id = it[ShoeVariationsTable.id],
                    size = it[ShoeVariationsTable.size],
                    quantity = it[ShoeVariationsTable.quantity],
                    color = it[ShoeVariationsTable.color],
                    price = it[ShoeVariationsTable.price],
                    image = it[ShoeVariationsTable.image]
                )
            }
        return Shoe(
            id = row[ShoesTable.id],
            name = row[ShoesTable.name],
            price = row[ShoesTable.price].toDouble(),
            productType = row[ShoesTable.productType],
            description = row[ShoesTable.description],
            variants = variants,
            images = images,
            brand = row[ShoesTable.brandId]?.let { brandId ->
                BrandsTable
                    .select { BrandsTable.id eq brandId }
                    .map {
                        Brand(
                            id = it[BrandsTable.id],
                            name = it[BrandsTable.name],
                            description = it[BrandsTable.description],
                            logoUrl = it[BrandsTable.logoUrl],
                            shoes = ShoesTable.select { ShoesTable.brandId eq brandId }.count().toInt()
                        )
                    }
                    .singleOrNull()
            },
            category = CategoriesTable
                .select{ CategoriesTable.id eq row[ShoesTable.categoryId] }
                .map { it[CategoriesTable.name] }
                .single(),
            createdAt = row[ShoesTable.createdAt].toString(),
            isInWishList = true
        )
    }
}