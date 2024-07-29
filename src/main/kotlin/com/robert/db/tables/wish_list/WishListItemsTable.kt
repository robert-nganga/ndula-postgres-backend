package com.robert.db.tables.wish_list

import com.robert.db.tables.shoe.ShoesTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object WishListItemsTable : Table() {
    val id = integer("id").autoIncrement()
    val wishlistId = reference("wishlist_id", WishListTable.id)
    val shoeId = reference("shoe_id", ShoesTable.id)
    val createdAt = datetime("added_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(wishlistId, shoeId) // Prevent duplicate items in a wishlist
    }
}