package com.robert.db.dao.category

import com.robert.models.Category


interface CategoryDao {
    suspend fun getCategoryId(id: Int): Int?
    suspend fun insertCategory(category: Category): Category?
    suspend fun getAllCategories(): List<Category>
}