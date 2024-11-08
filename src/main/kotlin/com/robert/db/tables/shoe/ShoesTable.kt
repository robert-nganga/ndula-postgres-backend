package com.robert.db.tables.shoe

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object ShoesTable: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val price = decimal("price", 10, 2)
    val productType = varchar("product_type", 100)
    val categoryId = reference("category_id", CategoriesTable.id)
    val brandId = reference("brand_id", BrandsTable.id).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val averageRating = decimal("average_rating", precision = 3, scale = 2).default(0.toBigDecimal())
    val totalReviews = integer("total_reviews").default(0)

    override val primaryKey = PrimaryKey(id)
}