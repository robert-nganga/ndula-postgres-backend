package com.robert.db.tables.cart

import com.robert.db.tables.cart.CartItemsTable.autoIncrement
import com.robert.db.tables.cart.CartItemsTable.integer

object CartTable {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val createdAt = integer("created_at")
    val updatedAt = integer("updated_at")
}