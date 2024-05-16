package com.robert.db.tables.cart

import com.robert.db.tables.shoe.ShoesTable
import org.jetbrains.exposed.sql.Table

object CartItemsTable: Table() {
    val id = integer("id").autoIncrement()
    val cartId = integer("cart_id").references(CartTable.id)
    val shoeId = integer("shoe_id").references(ShoesTable.id)
    val quantity = integer("quantity")
}