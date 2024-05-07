package com.robert.db.dao.user

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.models.User
import com.robert.db.tables.UserTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class UserDaoImpl: UserDao {

    private fun resultRowToNode(row: ResultRow): User = User(
        id = row[UserTable.id],
        name = row[UserTable.name],
        password = row[UserTable.password],
        email = row[UserTable.email],
        image = row[UserTable.image],
        salt = row[UserTable.salt],
        createdAt = row[UserTable.created],
    )

    override suspend fun createUser(user: User): User? = dbQuery {
        val insertStatement = UserTable.insert {
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
        UserTable
            .select{ UserTable.email eq email }
            .map(::resultRowToNode)
            .singleOrNull()
    }
}