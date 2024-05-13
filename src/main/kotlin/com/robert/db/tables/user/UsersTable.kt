package com.robert.db.tables.user

import org.jetbrains.exposed.sql.Table

object UsersTable: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 256)
    val email = varchar("email", 128).uniqueIndex()
    val image = varchar("image", 1024)
    val password  = varchar("password", 256)
    val salt = varchar("salt", 256)
    val created = long("created")
    override val primaryKey = PrimaryKey(id)
}