package com.robert.db.tables.review

import com.robert.db.tables.shoe.ShoesTable
import com.robert.db.tables.user.UsersTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object ReviewsTable : Table() {
    val id = integer("id").autoIncrement()
    val userId = reference("user_id", UsersTable.id)
    val shoeId = reference("shoe_id", ShoesTable.id)
    val userName = varchar("user_name", 128)
    val userImage = varchar("user_image", 256)
    val rating = decimal("rating", 10, 2)
    val comment = text("comment")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(userId, shoeId, createdAt)  // Ensure one review per user per shoe
    }
}