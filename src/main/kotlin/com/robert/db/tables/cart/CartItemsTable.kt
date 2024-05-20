package com.robert.db.tables.cart

import com.robert.db.tables.shoe.ShoesTable
import org.jetbrains.exposed.sql.Table

object CartItemsTable: Table() {
    val id = integer("id").autoIncrement()
    val cartId = reference("cart_id", CartTable.id)
    val shoeId = reference("shoe_id", CartTable.id)
    val quantity = integer("quantity")
    override val primaryKey = PrimaryKey(id)
}