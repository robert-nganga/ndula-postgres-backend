package com.robert.db.tables.order

import com.robert.db.tables.shoe.ShoesTable.defaultExpression
import com.robert.db.tables.user.UsersTable
import com.robert.models.OrderStatus
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object OrdersTable : Table() {
    val id = integer("id").autoIncrement()
    val userId = reference("user_id", UsersTable.id)
    val totalAmount = decimal("total_amount", 10, 2)
    val status = enumerationByName("status", 20, OrderStatus::class)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}