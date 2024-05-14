package com.robert.repositories.shoe

import com.robert.db.dao.category.CategoryDao
import com.robert.models.Category
import com.robert.utils.BaseResponse
import io.ktor.http.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

class CategoryRepositoryImpl(
    private  val categoryDao: CategoryDao
): CategoryRepository{


    override suspend fun insertCategory(category: Category): BaseResponse<Category> {
        return try {
            val insertedCategory = categoryDao.insertCategory(category)
            if (insertedCategory == null)
                BaseResponse.ErrorResponse(message = "Error inserting category", status = HttpStatusCode.InternalServerError)
            else
                BaseResponse.SuccessResponse(data = insertedCategory)
        } catch (e: ExposedSQLException) {
            BaseResponse.ErrorResponse(message = "Category name already exists", status = HttpStatusCode.InternalServerError)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(message = "Error adding category", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getAllCategories(): BaseResponse<List<Category>> {
        return try {
            val categories = categoryDao.getAllCategories()
            BaseResponse.SuccessResponse(data = categories)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(message = "Error fetching categories", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getCategoryById(id: Int): BaseResponse<Category> {
        TODO("Not yet implemented")
    }
}