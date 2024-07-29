package com.robert.db.tables.wish_list

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object WishListTable : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(userId) // Each user can have only one wishlist
    }
}