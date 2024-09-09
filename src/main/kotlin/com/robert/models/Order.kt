package com.robert.models

data class Order(
    val id: Int,
    val userId: Int,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val shippingAddress: ShippingAddress,
    val createdAt: String
)



enum class OrderStatus {
    PAYMENTCONFIRMED, PROCESSING, INDELIVERY, COMPLETED
}
