package com.robert.db.tables.shoe

import org.jetbrains.exposed.sql.Table

object BrandsTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val logoUrl = varchar("logo_url", 1024).nullable()

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(name)
    }
}