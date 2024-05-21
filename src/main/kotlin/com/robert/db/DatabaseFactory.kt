package com.robert.db

import com.robert.db.tables.cart.CartItemsTable
import com.robert.db.tables.cart.CartTable
import com.robert.db.tables.order.OrderItemsTable
import com.robert.db.tables.order.OrdersTable
import com.robert.db.tables.shoe.*
import com.robert.db.tables.user.UsersTable
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
        Database.connect(createHikariDataSource())
        transaction {
            SchemaUtils.create(UsersTable)
            SchemaUtils.create(BrandsTable)
            SchemaUtils.create(CategoriesTable)
            SchemaUtils.create(ShoesTable)
            SchemaUtils.create(ShoeImagesTable)
            SchemaUtils.create(ShoeSizesTable)
            SchemaUtils.create(CartTable)
            SchemaUtils.create(CartItemsTable)
            SchemaUtils.create(OrdersTable)
            SchemaUtils.create(OrderItemsTable)
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