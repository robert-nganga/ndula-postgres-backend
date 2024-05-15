package com.robert.db.dao.category

import com.robert.models.Category


interface CategoryDao {
    suspend fun getCategoryId(name: String): Int?
    suspend fun insertCategory(category: Category): Category?
    suspend fun getAllCategories(): List<Category>
}