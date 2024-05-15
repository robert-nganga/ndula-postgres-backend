package com.robert.db.dao.brand

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.tables.shoe.BrandsTable
import com.robert.models.Brand
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class BrandDaoImpl:  BrandDao{
    private fun resultRowToBrand(row: ResultRow): Brand = Brand(
            id = row[BrandsTable.id],
            name = row[BrandsTable.name],
            description = row[BrandsTable.description],
            logoUrl = row[BrandsTable.logoUrl]
        )

    override suspend fun insertBrand(brand: Brand): Brand? = dbQuery {
        val insertStatement = BrandsTable.insert {
            it[name] = brand.name
            it[description] = brand.description
            it[logoUrl] = brand.logoUrl
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToBrand)
    }

    override suspend fun getBrandId(name: String): Int? = dbQuery {
        BrandsTable
            .select { BrandsTable.name.eq(name) }
            .map { it[BrandsTable.id] }
            .singleOrNull()
    }

    override suspend fun getAllBrands(): List<Brand> = dbQuery {
        BrandsTable
            .selectAll()
            .map(::resultRowToBrand)
    }
}