package com.robert.db

import com.robert.db.tables.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    private  val jdbcUrl = System.getenv("JDBC_DATABASE_URL")
    private  val driver = System.getenv("JDBC_DATABASE_DRIVER")
    private  val user = System.getenv("JDBC_DATABASE_USERNAME")
    private  val password = System.getenv("JDBC_DATABASE_PASSWORD")


    fun init() {
        val database =  Database.connect(createHikariDataSource())
        transaction {
            SchemaUtils.create(UsersTable)
        }
    }

    private fun createHikariDataSource(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = "jdbc:postgresql://localhost:5432/shoesdb"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.password = "password"
        config.username = "postgres"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: suspend  () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}