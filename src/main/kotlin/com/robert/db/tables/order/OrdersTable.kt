package com.robert.db.tables.order

import com.robert.db.tables.user.UsersTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object OrdersTable : Table() {
    val id = integer("id").autoIncrement()
    val userId = reference("user_id", UsersTable.id)
    val total = decimal("total", 10, 2)
    val status = varchar("status", 50)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}