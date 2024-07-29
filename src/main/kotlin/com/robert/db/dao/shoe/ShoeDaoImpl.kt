package com.robert.db.dao.shoe

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.dao.wish_list.WishListDao
import com.robert.db.tables.shoe.*
import com.robert.models.Brand
import com.robert.models.PaginatedShoes
import com.robert.models.Shoe
import com.robert.models.ShoeVariant
import com.robert.request.ShoeRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ShoeDaoImpl(private val wishListDao: WishListDao): ShoeDao {

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
                    price = it[ShoeVariationsTable.price],
                    image = it[ShoeVariationsTable.image]
                )
            }

        val brand = row[ShoesTable.brandId]?.let { brandId ->
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
        }

        return Shoe(
            id = shoeId,
            name = row[ShoesTable.name],
            price = row[ShoesTable.price].toDouble(),
            productType = row[ShoesTable.productType],
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

    override suspend fun insertShoe(shoe: ShoeRequest): Shoe? = dbQuery {
        val insertStatement = ShoesTable.insert { insertStatement ->
            insertStatement[name] = shoe.name
            insertStatement[productType] = shoe.productType
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

        shoe.variants.forEach { shoeVariant ->
            ShoeVariationsTable.insert {
                it[productId] = shoeId
                it[image] = shoeVariant.image
                it[size] = shoeVariant.size
                it[quantity] = shoeVariant.quantity
                it[price] = shoeVariant.price
                it[color] = shoeVariant.color
            }
        }

        ShoesTable
            .select { ShoesTable.id eq shoeId }
            .map{resultRowToShoe(it)}
            .singleOrNull()
    }

    override suspend fun searchShoes(query: String, userId: Int?): List<Shoe> = dbQuery {
        val queryLower = query.lowercase()
        ShoesTable
        .select { ShoesTable.name.lowerCase() like "%$queryLower%" }
            .map{
                val shoe = resultRowToShoe(it)
                shoe.copy(
                    isInWishList = wishListDao.isShoeInWishlist(userId = userId, shoeId = shoe.id)
                )
            }
    }

    override suspend fun getShoeById(id: Int, userId: Int?): Shoe = dbQuery {
        ShoesTable
            .select { ShoesTable.id eq id }
            .map{
                val shoe = resultRowToShoe(it)
                shoe.copy(
                    isInWishList = wishListDao.isShoeInWishlist(userId = userId, shoeId = shoe.id)
                )
            }
            .single()
    }

    override suspend fun getAllShoesPaginated(page: Int, pageSize: Int, userId: Int?): PaginatedShoes = dbQuery {
        val offset = (page - 1) * pageSize
        val totalCount = ShoesTable.selectAll().count().toInt()
        val shoes = ShoesTable
            .selectAll()
            .limit(pageSize, offset.toLong())
            .map{
                val shoe = resultRowToShoe(it)
                shoe.copy(
                    isInWishList = wishListDao.isShoeInWishlist(userId = userId, shoeId = shoe.id)
                )
            }

        PaginatedShoes(shoes, totalCount)
    }

    override suspend fun filterShoesByBrand(brand: String, userId: Int?): List<Shoe> = dbQuery {
        ShoesTable
            .select { ShoesTable.brandId inSubQuery(
                    BrandsTable
                        .slice(BrandsTable.id)
                        .select { BrandsTable.name eq brand }
                    ) }
            .map{
                val shoe = resultRowToShoe(it)
                shoe.copy(
                    isInWishList = wishListDao.isShoeInWishlist(userId = userId, shoeId = shoe.id)
                )
            }
    }

    override suspend fun filterShoesByCategory(category: String, userId: Int?): List<Shoe> = dbQuery {
        ShoesTable
            .leftJoin(CategoriesTable, { categoryId }, { id })
            .select { CategoriesTable.name eq category }
            .map { resultRow ->
                val shoe = resultRowToShoe(resultRow)
                shoe.copy(
                    isInWishList = wishListDao.isShoeInWishlist(userId = userId, shoeId = shoe.id)
                )
            }
    }

    override suspend fun updateShoe(shoe: Shoe): Shoe? = dbQuery {
        val updatedCount = ShoesTable.update({ ShoesTable.id eq shoe.id }) { updateStatement ->
            updateStatement[name] = shoe.name
            updateStatement[price] = shoe.price.toBigDecimal()
            updateStatement[productType] = shoe.productType
            updateStatement[description] = shoe.description
            updateStatement[categoryId] = CategoriesTable
                .select { CategoriesTable.name eq shoe.category }
                .map { it[CategoriesTable.id] }
                .singleOrNull() ?: return@update
            updateStatement[brandId] = shoe.brand?.let { brand ->
                BrandsTable
                    .select { BrandsTable.name eq brand.name }
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
            shoe.variants.forEach { shoeVariant ->
                ShoeVariationsTable.insert {
                    it[productId] = shoe.id
                    it[image] = shoeVariant.image
                    it[size] = shoeVariant.size
                    it[quantity] = shoeVariant.quantity
                    it[color] = shoeVariant.color
                    it[price] = shoeVariant.price
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