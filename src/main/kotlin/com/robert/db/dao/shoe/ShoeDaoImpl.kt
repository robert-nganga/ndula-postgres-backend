package com.robert.db.dao.shoe

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.tables.shoe.*
import com.robert.models.PaginatedShoes
import com.robert.models.Shoe
import com.robert.models.ShoeSize
import org.jetbrains.exposed.sql.*

class ShoeDaoImpl: ShoeDao {

    private fun resultRowToShoe(row: ResultRow): Shoe {
        val shoeId = row[ShoesTable.id]

        val images = ShoeImagesTable
            .select{ ShoeImagesTable.productId eq shoeId }
            .map{ it[ShoeImagesTable.imageUrl] }

        val sizes = ShoeSizesTable
            .select{ ShoeSizesTable.productId eq shoeId }
            .map {
                ShoeSize(
                    size = it[ShoeSizesTable.size],
                    quantity = it[ShoeSizesTable.quantity]
                )
            }

        val brand = row[ShoesTable.brandId]?.let { brandId ->
            BrandsTable
                .select { BrandsTable.id eq brandId }
                .map { it[BrandsTable.name] }
                .singleOrNull()
        }

        return Shoe(
            id = shoeId,
            name = row[ShoesTable.name],
            description = row[ShoesTable.description],
            price = row[ShoesTable.price].toDouble(),
            category = CategoriesTable
                .select{ CategoriesTable.id eq row[ShoesTable.categoryId] }
                .map { it[CategoriesTable.name] }
                .single(),
            brand = brand,
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
                .select { CategoriesTable.name eq shoe.category }
                .map { it[CategoriesTable.id] }
                .singleOrNull() ?: return@insert
            insertStatement[brandId] = shoe.brand?.let { brand ->
                BrandsTable
                    .select { BrandsTable.name eq brand }
                    .map { it[BrandsTable.id] }
                    .singleOrNull()
            }
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
            .select { ShoesTable.id eq shoeId }
            .map(::resultRowToShoe)
            .singleOrNull()
    }

    override suspend fun getShoeById(id: Int): Shoe? = dbQuery {
        ShoesTable
            .select { ShoesTable.id eq id }
            .map(::resultRowToShoe)
            .singleOrNull()
    }

    override suspend fun getAllShoesPaginated(page: Int, pageSize: Int): PaginatedShoes = dbQuery {
        val offset = (page - 1) * pageSize
        val totalCount = ShoesTable.selectAll().count().toInt()
        val shoes = ShoesTable
            .selectAll()
            .limit(pageSize, offset.toLong())
            .map(::resultRowToShoe)

        PaginatedShoes(shoes, totalCount)
    }

    override suspend fun filterShoesByBrand(brand: String): List<Shoe> = dbQuery {
        ShoesTable
            .select { ShoesTable.brandId inSubQuery(
                    BrandsTable
                        .slice(BrandsTable.id)
                        .select { BrandsTable.name eq brand }
                    ) }
            .map(::resultRowToShoe)
    }

    override suspend fun filterShoesByCategory(category: String): List<Shoe> = dbQuery {
        ShoesTable
            .leftJoin(CategoriesTable, { categoryId }, { id })
            .select { CategoriesTable.name eq category }
            .map { resultRow ->
                resultRowToShoe(resultRow)
            }
    }

    override suspend fun updateShoe(shoe: Shoe): Shoe? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteShoe(shoe: Shoe): Boolean {
        TODO("Not yet implemented")
    }
}