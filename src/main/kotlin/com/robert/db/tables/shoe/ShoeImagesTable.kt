package com.robert.db.tables.shoe

import org.jetbrains.exposed.sql.Table

object ShoeImagesTable: Table() {
    val id = integer("id").autoIncrement()
    val productId = reference("product_id", ShoesTable.id)
    val imageUrl = varchar("image_url", 1024)

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(productId, imageUrl)
    }
}