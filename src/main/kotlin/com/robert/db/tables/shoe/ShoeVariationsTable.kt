package com.robert.db.tables.shoe

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ShoeVariationsTable: Table() {
    val id = integer("id").autoIncrement()
    val image = varchar("image", 1024).nullable()
    val productId = reference("product_id", ShoesTable.id, onDelete = ReferenceOption.CASCADE)
    val size = integer("size")
    val color = varchar("color", 256)
    val quantity = integer("quantity")
    val price = double("price")
    override val primaryKey = PrimaryKey(id)

    // Unique constraint on (product_id, size) combination
    init {
        uniqueIndex(productId, size, color)
    }
}