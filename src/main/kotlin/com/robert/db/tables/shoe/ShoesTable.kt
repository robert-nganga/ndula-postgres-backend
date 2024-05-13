package com.robert.db.tables.shoe

import org.jetbrains.exposed.sql.Table

object ShoesTable: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val price = decimal("price", 10, 2)
    val category = varchar("category", 255)

    override val primaryKey = PrimaryKey(id) // Set the primary key
}