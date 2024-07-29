package com.robert.repositories.shoe

import com.robert.db.dao.shoe.ShoeDao
import com.robert.models.PaginatedShoes
import com.robert.models.Shoe
import com.robert.request.ShoeRequest
import com.robert.utils.BaseResponse
import io.ktor.http.*

class ShoeRepositoryImpl(
    private val shoeDao: ShoeDao
) : ShoeRepository {

    override suspend fun insertShoe(shoe: ShoeRequest): BaseResponse<Shoe> {
        return try {
            val insertedShoe = shoeDao.insertShoe(shoe)
            if (insertedShoe != null) {
                BaseResponse.SuccessResponse(data = insertedShoe, status = HttpStatusCode.Created)
            } else {
                BaseResponse.ErrorResponse("Failed to insert shoe", HttpStatusCode.BadRequest)
            }
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getShoeById(id: Int, userId: Int?): BaseResponse<Shoe> {
        return try {
            val shoe = shoeDao.getShoeById(id, userId)
            BaseResponse.SuccessResponse(shoe)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun searchShoes(query: String, userId: Int?): BaseResponse<List<Shoe>> {
        return try {
            val shoes = shoeDao.searchShoes(query, userId)
            BaseResponse.SuccessResponse(shoes)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getAllShoesPaginated(page: Int, pageSize: Int, userId: Int?): BaseResponse<PaginatedShoes> {
        return try {
            val paginatedShoes = shoeDao.getAllShoesPaginated(page, pageSize, userId)
            BaseResponse.SuccessResponse(data = paginatedShoes)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun filterShoesByBrand(brand: String, userId: Int?): BaseResponse<List<Shoe>> {
        return try {
            val shoes = shoeDao.filterShoesByBrand(brand, userId)
            BaseResponse.SuccessResponse(shoes)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun filterShoesByCategory(category: String, userId: Int?): BaseResponse<List<Shoe>> {
        return try {
            val shoes = shoeDao.filterShoesByCategory(category, userId)
            BaseResponse.SuccessResponse(shoes)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }


    override suspend fun updateShoe(shoe: Shoe): BaseResponse<Shoe> {
        return try {
            val updatedShoe = shoeDao.updateShoe(shoe)
            if (updatedShoe != null) {
                BaseResponse.SuccessResponse(data = updatedShoe)
            } else {
                BaseResponse.ErrorResponse("Failed to update shoe", HttpStatusCode.BadRequest)
            }
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun deleteShoe(id: Int): BaseResponse<Shoe> {
        return try {
            val shoe = shoeDao.getShoeById(id, null)
            val isDeleted = shoeDao.deleteShoe(id)
            if (isDeleted) {
                BaseResponse.SuccessResponse(data = shoe)
            } else {
                BaseResponse.ErrorResponse("Failed to delete shoe", HttpStatusCode.BadRequest)
            }
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }

    }
}