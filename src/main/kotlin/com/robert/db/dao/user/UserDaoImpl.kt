package com.robert.db.dao.user

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.models.User
import com.robert.db.tables.user.UsersTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class UserDaoImpl: UserDao {

    private fun resultRowToNode(row: ResultRow): User = User(
        id = row[UsersTable.id],
        name = row[UsersTable.name],
        password = row[UsersTable.password],
        email = row[UsersTable.email],
        image = row[UsersTable.image],
        salt = row[UsersTable.salt],
        createdAt = row[UsersTable.created],
    )

    override suspend fun createUser(user: User): User? = dbQuery {
        val insertStatement = UsersTable.insert {
            it[name] = user.name
            it[password] = user.password
            it[email] = user.email
            it[image] = user.image
            it[salt] = user.salt
            it[created] = user.createdAt
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToNode)
    }


    override suspend fun findUserByEmail(email: String): User? = dbQuery {
        UsersTable
            .select{ UsersTable.email eq email }
            .map(::resultRowToNode)
            .singleOrNull()
    }
}