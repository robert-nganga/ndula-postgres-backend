package com.robert.db

import com.robert.db.tables.cart.CartItemsTable
import com.robert.db.tables.cart.CartTable
import com.robert.db.tables.order.OrderItemsTable
import com.robert.db.tables.order.OrdersTable
import com.robert.db.tables.order.ShippingAddressesTable
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

    private  val jdbcUrl = System.getenv("JDBC_DATABASE_URL")
    private  val driver = System.getenv("JDBC_DATABASE_DRIVER")
    private  val user = System.getenv("JDBC_DATABASE_USERNAME")
    private  val password = System.getenv("JDBC_DATABASE_PASSWORD")

    private const val NEON_DB_URL = "jdbc:postgresql://ep-super-rice-a2zizsat.eu-central-1.aws.neon.tech/nduladb?user=nduladb_owner&password=rp8Jiwhtn7ZH&sslmode=require"

    fun init() {
        Database.connect(createHikariDataSource())
        transaction {
            SchemaUtils.create(UsersTable)
            SchemaUtils.create(BrandsTable)
            SchemaUtils.create(CategoriesTable)
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

//    val config = HikariConfig()
//    config.driverClassName = "org.postgresql.Driver"
//    config.jdbcUrl = NEON_DB_URL
//    config.maximumPoolSize = 3
//    config.isAutoCommit = false
//    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
//    config.validate()

    suspend fun <T> dbQuery(block: suspend  () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}