package com.robert.db.tables.order

import org.jetbrains.exposed.sql.Table

object ShippingAddressesTable : Table() {
    val id = integer("id").autoIncrement()
    val orderId = reference("order_id", OrdersTable.id)
    val name = varchar("name", 255)
    val formattedAddress = varchar("formatted_address", 512)
    val lat = double("lat")
    val lng = double("lng")
    val placeId = varchar("place_id", 255)
    val phoneNumber = varchar("phone_number", 20)
    val floorNumber = varchar("floor_number", 10)
    val doorNumber = varchar("door_number", 10)
    val buildingName = varchar("building_name", 255)

    override val primaryKey = PrimaryKey(id)
}
