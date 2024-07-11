package com.robert.db.dao.shoe

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.tables.shoe.*
import com.robert.models.PaginatedShoes
import com.robert.models.Shoe
import com.robert.models.ShoeVariant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ShoeDaoImpl: ShoeDao {

    override fun resultRowToShoe(row: ResultRow): Shoe {
        val shoeId = row[ShoesTable.id]

        val images = ShoeImagesTable
            .select{ ShoeImagesTable.productId eq shoeId }
            .map{ it[ShoeImagesTable.imageUrl] }

        val variants = ShoeVariationsTable
            .select{ ShoeVariationsTable.productId eq shoeId }
            .map {
                ShoeVariant(
                    id = it[ShoeVariationsTable.id],
                    size = it[ShoeVariationsTable.size],
                    quantity = it[ShoeVariationsTable.quantity],
                    color = it[ShoeVariationsTable.color],
                    price = it[ShoeVariationsTable.price]
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
            category = CategoriesTable
                .select{ CategoriesTable.id eq row[ShoesTable.categoryId] }
                .map { it[CategoriesTable.name] }
                .single(),
            brand = brand,
            images = images,
            variants = variants,
            createdAt = row[ShoesTable.createdAt].toString()
        )
    }

    override suspend fun insertShoe(shoe: Shoe): Shoe? = dbQuery {
        val insertStatement = ShoesTable.insert { insertStatement ->
            insertStatement[name] = shoe.name
            insertStatement[description] = shoe.description
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

        shoe.variants.forEach { shoeVariant ->
            ShoeVariationsTable.insert {
                it[productId] = shoeId
                it[size] = shoeVariant.size
                it[quantity] = shoeVariant.quantity
                it[price] = shoeVariant.price
                it[color] = shoeVariant.color
            }
        }

        ShoesTable
            .select { ShoesTable.id eq shoeId }
            .map(::resultRowToShoe)
            .singleOrNull()
    }

    override suspend fun searchShoes(query: String): List<Shoe> = dbQuery {
        val queryLower = query.lowercase()
        ShoesTable
        .select { ShoesTable.name.lowerCase() like "%$queryLower%" }
            .map(::resultRowToShoe)
    }

    override suspend fun getShoeById(id: Int): Shoe = dbQuery {
        ShoesTable
            .select { ShoesTable.id eq id }
            .map(::resultRowToShoe)
            .single()
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

    override suspend fun updateShoe(shoe: Shoe): Shoe? = dbQuery {
        val updatedCount = ShoesTable.update({ ShoesTable.id eq shoe.id }) { updateStatement ->
            updateStatement[name] = shoe.name
            updateStatement[description] = shoe.description
            updateStatement[categoryId] = CategoriesTable
                .select { CategoriesTable.name eq shoe.category }
                .map { it[CategoriesTable.id] }
                .singleOrNull() ?: return@update
            updateStatement[brandId] = shoe.brand?.let { brand ->
                BrandsTable
                    .select { BrandsTable.name eq brand }
                    .map { it[BrandsTable.id] }
                    .singleOrNull() ?: return@update
            }
        }

        if (updatedCount > 0) {
            // Update shoe images
            ShoeImagesTable.deleteWhere { productId eq shoe.id }
            shoe.images.forEach { image ->
                ShoeImagesTable.insert {
                    it[productId] = shoe.id
                    it[imageUrl] = image
                }
            }

            // Update shoe variants
            ShoeVariationsTable.deleteWhere { productId eq shoe.id }
            shoe.variants.forEach { shoeSize ->
                ShoeVariationsTable.insert {
                    it[productId] = shoe.id
                    it[size] = shoeSize.size
                    it[quantity] = shoeSize.quantity
                    it[color] = shoeSize.color
                    it[price] = shoeSize.price
                }
            }

            ShoesTable
                .select { ShoesTable.id eq shoe.id }
                .map(::resultRowToShoe)
                .singleOrNull()
        } else {
            null
        }
    }

    override suspend fun deleteShoe(shoeId: Int): Boolean = dbQuery{
        val deletedRows = ShoesTable.deleteWhere { id eq shoeId }

        if (deletedRows > 0) {
            // Delete related data
            ShoeImagesTable.deleteWhere { productId eq shoeId }
            ShoeVariationsTable.deleteWhere { productId eq shoeId }
            true
        } else {
            false
        }
    }
}