package com.robert.db.tables.shoe

import org.jetbrains.exposed.sql.Table

object CategoriesTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val image = text("image_url").default("")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(name)
    }
}


