package com.robert.db.tables.user

import com.robert.db.tables.cart.CartTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 256)
    val email = varchar("email", 128).uniqueIndex()
    val image = varchar("image", 1024)
    val password  = varchar("password", 256)
    val salt = varchar("salt", 256)
    val cartId = integer("cart_id").references(CartTable.id)
    val created = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}