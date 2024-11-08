package com.robert.db

import com.robert.db.tables.cart.CartItemsTable
import com.robert.db.tables.cart.CartTable
import com.robert.db.tables.order.OrderItemsTable
import com.robert.db.tables.order.OrdersTable
import com.robert.db.tables.order.ShippingAddressesTable
import com.robert.db.tables.review.ReviewsTable
import com.robert.db.tables.shoe.*
import com.robert.db.tables.user.UsersTable
import com.robert.db.tables.wish_list.WishListItemsTable
import com.robert.db.tables.wish_list.WishListTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    private  val jdbcUrl = "jdbc:postgresql://localhost:5432/shoesdb"
    private  val driver = "org.postgresql.Driver"
    private  val user = "postgres"
    private  val password = System.getenv("DATABASE_PASSWORD")


    fun init() {
        Database.connect(createHikariDataSource())
        transaction {
            SchemaUtils.create(UsersTable)
            SchemaUtils.create(BrandsTable)
            SchemaUtils.createMissingTablesAndColumns(CategoriesTable)
            SchemaUtils.create(ShoesTable)
            SchemaUtils.create(ShoeImagesTable)
            SchemaUtils.create(ShoeVariationsTable)
            SchemaUtils.create(CartTable)
            SchemaUtils.create(CartItemsTable)
            SchemaUtils.create(OrdersTable)
            SchemaUtils.create(OrderItemsTable)
            SchemaUtils.create(WishListTable)
            SchemaUtils.create(WishListItemsTable)
            SchemaUtils.create(ShippingAddressesTable)
            SchemaUtils.create(ReviewsTable)
        }
    }

    private fun createHikariDataSource(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = driver
        config.jdbcUrl = jdbcUrl
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.password = password
        config.username = user
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: suspend  () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}