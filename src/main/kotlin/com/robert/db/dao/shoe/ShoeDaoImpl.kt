package com.robert.db.dao.shoe

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.tables.shoe.*
import com.robert.models.Shoe
import com.robert.models.ShoeSize
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ShoeDaoImpl: ShoeDao {

    private fun resultRowToShoe(row: ResultRow): Shoe {
        val shoeId = row[ShoesTable.id]

        val images = ShoeImagesTable
            .selectAll().where{ ShoeImagesTable.productId eq shoeId }
            .map{ it[ShoeImagesTable.imageUrl] }

        val sizes = ShoeSizesTable
            .selectAll().where { ShoeSizesTable.productId eq shoeId }
            .map {
                ShoeSize(
                    size = it[ShoeSizesTable.size],
                    quantity = it[ShoeSizesTable.quantity]
                )
            }

        return Shoe(
            id = shoeId,
            name = row[ShoesTable.name],
            description = row[ShoesTable.description],
            price = row[ShoesTable.price].toDouble(),
            category = CategoriesTable
                .selectAll().where { CategoriesTable.id eq row[ShoesTable.categoryId] }
                .map { it[CategoriesTable.name] }
                .single(),
            brand = BrandsTable
                    .selectAll().where { BrandsTable.id eq row[ShoesTable.brandId] }
                    .map { it[BrandsTable.name] }
                    .single(),
            images = images,
            sizes = sizes,
            createdAt = row[ShoesTable.createdAt].toString()
        )
    }
    override suspend fun insertShoe(shoe: Shoe): Shoe? = dbQuery{
        val insertStatement = ShoesTable.insert { insertStatement ->
            insertStatement[name] = shoe.name
            insertStatement[description] = shoe.description
            insertStatement[price] = shoe.price.toBigDecimal()
            insertStatement[categoryId] = CategoriesTable
                .selectAll().where { CategoriesTable.name eq shoe.category }
                .map { it[CategoriesTable.id] }
                .singleOrNull() ?: return@insert
            insertStatement[brandId] = BrandsTable
                .selectAll().where { BrandsTable.name eq shoe.brand }
                .map { it[BrandsTable.id] }
                .singleOrNull() ?: return@insert
        }

        val shoeId = insertStatement.resultedValues?.singleOrNull()?.let { it[ShoesTable.id] } ?: return@dbQuery null

        shoe.images.forEach { image->
            ShoeImagesTable.insert {
                it[productId] = shoeId
                it[imageUrl] = image
            }
        }

        shoe.sizes.forEach { shoeSize ->
            ShoeSizesTable.insert {
                it[productId] = shoeId
                it[size] = shoeSize.size
                it[quantity] = shoeSize.quantity
            }
        }

        ShoesTable
            .selectAll().where { ShoesTable.id eq shoeId }
            .map(::resultRowToShoe)
            .singleOrNull()
    }

    override suspend fun getShoeById(id: Int): Shoe? {
        TODO("Not yet implemented")
    }

    override suspend fun getShoesByBrand(brand: String): List<Shoe> {
        TODO("Not yet implemented")
    }

    override suspend fun updateShoe(shoe: Shoe): Shoe? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteShoe(shoe: Shoe): Boolean {
        TODO("Not yet implemented")
    }
}