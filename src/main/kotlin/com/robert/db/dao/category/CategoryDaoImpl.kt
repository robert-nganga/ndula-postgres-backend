package com.robert.db.dao.category

import com.robert.db.DatabaseFactory.dbQuery
import com.robert.db.tables.shoe.CategoriesTable
import com.robert.models.Category
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class CategoryDaoImpl: CategoryDao {

    private fun resultRowToCategory(row: ResultRow): Category = Category(
        id = row[CategoriesTable.id],
        name = row[CategoriesTable.name],
        description = row[CategoriesTable.description]
    )
    override suspend fun getCategoryId(name: String): Int? = dbQuery {
        CategoriesTable
            .select{ CategoriesTable.name eq name }
            .map { it[CategoriesTable.id] }
            .singleOrNull()
    }

    override suspend fun insertCategory(category: Category): Category? = dbQuery {
        val insertStatement = CategoriesTable.insert {
            it[name] = category.name
            it[description] = category.description
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToCategory)
    }

    override suspend fun getAllCategories(): List<Category> = dbQuery {
        CategoriesTable
            .selectAll().map(::resultRowToCategory)
    }


}