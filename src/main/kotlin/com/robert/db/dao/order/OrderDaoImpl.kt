package com.robert.db.dao.order

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.tables.order.OrderItemsTable
import com.robert.db.tables.order.OrdersTable
import com.robert.db.tables.order.ShippingAddressesTable
import com.robert.db.tables.shoe.*
import com.robert.models.*
import com.robert.request.OrderRequest
import com.robert.response.OutOfStockError
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class OrderDaoImpl: OrderDao{

    override suspend fun createOrder(order: OrderRequest, currentUserId: Int): Order = dbQuery {
        transaction {
            // Check inventory and update quantities
            val errors = mutableListOf<OutOfStockError>()
            order.items.forEach { item ->
                val currentQuantity = ShoeVariationsTable
                    .select { (ShoeVariationsTable.id eq item.variantId) }
                    .map { it[ShoeVariationsTable.quantity] }
                    .singleOrNull() ?: throw Exception("Variant not found")

                if (currentQuantity < item.quantity) {
                    val message = if (currentQuantity <= 0) "This item is out of stock"
                    else "Only $currentQuantity items in stock"
                    errors.add(OutOfStockError(
                        message = message,
                        shoeId = item.shoeId,
                        variationId = item.variantId)
                    )
                }
            }
            if (errors.isNotEmpty()){
                throw InsufficientInventoryException(convertErrorsToJson(errors))
            }

            order.items.forEach { item ->
                val currentQuantity = ShoeVariationsTable
                    .select { (ShoeVariationsTable.id eq item.variantId) }
                    .map { it[ShoeVariationsTable.quantity] }
                    .singleOrNull() ?: throw Exception("Variant not found")

                ShoeVariationsTable.update({ ShoeVariationsTable.id eq item.variantId }) {
                    it[quantity] = currentQuantity - item.quantity
                }
            }

            // Create the order
            val orderId = OrdersTable.insert {
                it[userId] = currentUserId
                it[totalAmount] = order.totalAmount.toBigDecimal()
                it[status] = order.status
                it[createdAt] = java.time.LocalDateTime.now()
            } get OrdersTable.id

            order.items.forEach { item ->
                OrderItemsTable.insert {
                    it[this.orderId] = orderId
                    it[shoeId] = item.shoeId
                    it[variantId] = item.variantId
                    it[quantity] = item.quantity
                    it[price] = item.price.toBigDecimal()
                }
            }

            ShippingAddressesTable.insert {
                it[this.orderId] = orderId
                it[name] = order.shippingAddress.name
                it[formattedAddress] = order.shippingAddress.formattedAddress
                it[lat] = order.shippingAddress.lat
                it[lng] = order.shippingAddress.lng
                it[placeId] = order.shippingAddress.placeId
                it[phoneNumber] = order.shippingAddress.phoneNumber
                it[floorNumber] = order.shippingAddress.floorNumber
                it[doorNumber] = order.shippingAddress.doorNumber
                it[buildingName] = order.shippingAddress.buildingName
            }

            OrdersTable.select { OrdersTable.id eq orderId }
                .map { resultRowToOrder(it) }
                .single()
        }
    }


    override suspend fun getOrderById(id: Int): Order = dbQuery {
        OrdersTable.select { OrdersTable.id eq id }
            .map { resultRowToOrder(it) }
            .single()
    }

    override suspend fun updateOrderStatus(id: Int, status: OrderStatus): Boolean = dbQuery {
        OrdersTable.update({ OrdersTable.id eq id }) {
            it[OrdersTable.status] = status
        } > 0
    }

    override suspend fun getOrdersForUser(userId: Int): List<Order> = dbQuery {
        OrdersTable.select { OrdersTable.userId eq userId }
            .mapNotNull { resultRowToOrder(it) }
    }

    override suspend fun getActiveOrders(userId: Int): List<Order> = dbQuery{
        println("Active orders for user $userId")
        OrdersTable
            .select { OrdersTable.userId eq  userId and (OrdersTable.status neq  OrderStatus.COMPLETED)  }
            .map { resultRowToOrder(it) }
    }

    override suspend fun getCompletedOrders(userId: Int): List<Order> = dbQuery{
        OrdersTable
            .select { OrdersTable.userId eq  userId and   (OrdersTable.status eq  OrderStatus.COMPLETED)  }
            .map { resultRowToOrder(it) }
    }

    override suspend fun getVariantQuantity(variantId: Int): Int = dbQuery {
        ShoeVariationsTable
            .select { ShoeVariationsTable.id eq variantId }
            .map { it[ShoeVariationsTable.quantity] }
            .single()
    }

    override suspend fun updateVariantQuantity(variantId: Int, newQuantity: Int): Int = dbQuery {
        ShoeVariationsTable.update({ ShoeVariationsTable.id eq variantId }) {
            it[quantity] = newQuantity
        }
    }

    private fun resultRowToOrder(row: ResultRow): Order {
        val orderId = row[OrdersTable.id]
        val items = OrderItemsTable.select { OrderItemsTable.orderId eq orderId }
            .map { itemRow ->
                val shoe = ShoesTable.select { ShoesTable.id eq itemRow[OrderItemsTable.shoeId] }
                    .map { resultRowToShoe(it) }
                    .single()

                OrderItem(
                    id = itemRow[OrderItemsTable.id],
                    orderId = orderId,
                    shoe = shoe,
                    variantId = itemRow[OrderItemsTable.variantId],
                    quantity = itemRow[OrderItemsTable.quantity],
                    price = itemRow[OrderItemsTable.price].toDouble(),
                    rating = itemRow[OrderItemsTable.rating]?.toDouble()
                )
            }

        val shippingAddress = ShippingAddressesTable.select { ShippingAddressesTable.orderId eq orderId }
            .map { addressRow ->
                ShippingAddress(
                    id = addressRow[ShippingAddressesTable.id],
                    name = addressRow[ShippingAddressesTable.name],
                    formattedAddress = addressRow[ShippingAddressesTable.formattedAddress],
                    lat = addressRow[ShippingAddressesTable.lat],
                    lng = addressRow[ShippingAddressesTable.lng],
                    placeId = addressRow[ShippingAddressesTable.placeId],
                    phoneNumber = addressRow[ShippingAddressesTable.phoneNumber],
                    floorNumber = addressRow[ShippingAddressesTable.floorNumber],
                    doorNumber = addressRow[ShippingAddressesTable.doorNumber],
                    buildingName = addressRow[ShippingAddressesTable.buildingName]
                )
            }.single()

        return Order(
            id = orderId,
            userId = row[OrdersTable.userId],
            items = items,
            totalAmount = row[OrdersTable.totalAmount].toDouble(),
            status = row[OrdersTable.status],
            shippingAddress = shippingAddress,
            createdAt = row[OrdersTable.createdAt].toString()
        )
    }

    private fun resultRowToShoe(row: ResultRow): Shoe {
        val images = ShoeImagesTable
            .select{ ShoeImagesTable.productId eq row[ShoesTable.id] }
            .map{ it[ShoeImagesTable.imageUrl] }

        val variants = ShoeVariationsTable
            .select{ ShoeVariationsTable.productId eq row[ShoesTable.id] }
            .map {
                ShoeVariant(
                    id = it[ShoeVariationsTable.id],
                    size = it[ShoeVariationsTable.size],
                    quantity = it[ShoeVariationsTable.quantity],
                    color = it[ShoeVariationsTable.color],
                    price = it[ShoeVariationsTable.price],
                    image = it[ShoeVariationsTable.image]
                )
            }
        return Shoe(
            id = row[ShoesTable.id],
            name = row[ShoesTable.name],
            price = row[ShoesTable.price].toDouble(),
            productType = row[ShoesTable.productType],
            description = row[ShoesTable.description],
            variants = variants,
            images = images,
            brand = row[ShoesTable.brandId]?.let { brandId ->
                BrandsTable
                    .select { BrandsTable.id eq brandId }
                    .map {
                        Brand(
                            id = it[BrandsTable.id],
                            name = it[BrandsTable.name],
                            description = it[BrandsTable.description],
                            logoUrl = it[BrandsTable.logoUrl],
                            shoes = ShoesTable.select { ShoesTable.brandId eq brandId }.count().toInt()
                        )
                    }
                    .singleOrNull()
            },
            category = CategoriesTable
                .select{ CategoriesTable.id eq row[ShoesTable.categoryId] }
                .map { it[CategoriesTable.name] }
                .single(),
            createdAt = row[ShoesTable.createdAt].toString(),
            averageRating = row[ShoesTable.averageRating].toDouble(),
            totalReviews = row[ShoesTable.totalReviews],
            isInWishList = true
        )
    }

    private fun convertErrorsToJson(errors: List<OutOfStockError>): String {
        val mapper = jacksonObjectMapper().apply {
            registerModule(kotlinModule())
        }
        return mapper.writeValueAsString(errors)
    }

}

class InsufficientInventoryException(message: String) : Exception(message)