package com.robert.repositories.shoe

import com.robert.models.Category
import com.robert.utils.BaseResponse

interface CategoryRepository {
    suspend fun insertCategory(category: Category): BaseResponse<Category>
    suspend fun getAllCategories(): BaseResponse<List<Category>>
    suspend fun getCategoryById(id: Int): BaseResponse<Category>
}
