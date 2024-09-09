package com.robert.db.tables.order

import com.robert.db.tables.shoe.ShoeVariationsTable
import com.robert.db.tables.shoe.ShoesTable
import org.jetbrains.exposed.sql.Table

object OrderItemsTable : Table() {
    val id = integer("id").autoIncrement()
    val orderId = reference("order_id", OrdersTable.id)
    val shoeId = reference("shoe_id", ShoesTable.id)
    val variantId = reference("variant_id", ShoeVariationsTable.id)
    val quantity = integer("quantity")
    val price = decimal("price", 10, 2)

    override val primaryKey = PrimaryKey(id)
}