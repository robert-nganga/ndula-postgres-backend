package com.robert.db.tables.shoe

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ShoeSizesTable: Table() {
    val id = integer("id").autoIncrement()
    val productId = reference("product_id", ShoesTable.id, onDelete = ReferenceOption.CASCADE)
    val size = varchar("size", 10)
    val quantity = integer("quantity")

    override val primaryKey = PrimaryKey(id)

    // Unique constraint on (product_id, size) combination
    init {
        uniqueIndex(productId, size)
    }
}