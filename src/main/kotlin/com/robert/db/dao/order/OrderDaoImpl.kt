package com.robert.db.dao.order

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.dao.shoe.ShoeDao
import com.robert.db.tables.order.OrderItemsTable
import com.robert.db.tables.order.OrdersTable
import com.robert.models.Order
import com.robert.models.OrderItem
import com.robert.request.OrderRequest
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class OrderDaoImpl(
    private val shoeDao: ShoeDao
): OrderDao {

    private suspend fun resultRowToOrder(resultRow: ResultRow): Order {
        val orderId = resultRow[OrdersTable.id]

        val orderItems = OrderItemsTable
            .select { OrderItemsTable.orderId eq orderId }
            .map { itemRow ->
                val shoe = shoeDao.getShoeById(itemRow[OrderItemsTable.shoeId])
                OrderItem(
                    id = itemRow[OrderItemsTable.id],
                    orderId = orderId,
                    shoe = shoe,
                    quantity = itemRow[OrderItemsTable.quantity],
                    price = itemRow[OrderItemsTable.price].toDouble()
                )
            }

        return Order(
            id = orderId,
            userId = resultRow[OrdersTable.userId],
            items = orderItems,
            total = resultRow[OrdersTable.total].toDouble(),
            status = resultRow[OrdersTable.status],
            createdAt = resultRow[OrdersTable.createdAt].toString(),
            updatedAt = resultRow[OrdersTable.updatedAt].toString()
        )
    }
    override suspend fun createOrder(orderRequest: OrderRequest): Order = dbQuery {
        val orderId = OrdersTable.insert {
            it[userId] = orderRequest.userId
            it[total] = orderRequest.total.toBigDecimal()
            it[status] = orderRequest.status
            it[createdAt] = CurrentDateTime
            it[updatedAt] = CurrentDateTime
        } get OrdersTable.id


        orderRequest.items.forEach { item->
            OrderItemsTable.insert {
                it[OrderItemsTable.orderId] = orderId
                it[shoeId] = item.shoeId
                it[quantity] = item.quantity
                it[price] = item.price.toBigDecimal()
            } get OrderItemsTable.id
        }

        OrdersTable
            .select { OrdersTable.id eq orderId }
            .map { order->
                resultRowToOrder(order)
            }
            .single()
    }

    override suspend fun getOrderById(orderId: Int): Order = dbQuery{
        OrdersTable
            .select { OrdersTable.id eq orderId }
            .map { order->
                resultRowToOrder(order)
            }
            .single()
    }

    override suspend fun getAllOrdersForUser(userId: Int): List<Order> = dbQuery{
        OrdersTable
            .select { OrdersTable.userId eq userId }
            .map { order->
                resultRowToOrder(order)
            }

    }

    override suspend fun updateOrderStatus(orderId: Int, newStatus: String): Order = dbQuery {
        OrdersTable.update({ OrdersTable.id eq orderId }) {
            it[status] = newStatus
            it[updatedAt] = CurrentDateTime
        }

        OrdersTable
            .select { OrdersTable.id eq orderId }
            .map { order->
                resultRowToOrder(order)
            }
            .single()
    }

}