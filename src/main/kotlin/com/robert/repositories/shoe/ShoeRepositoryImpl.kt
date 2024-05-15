package com.robert.repositories.shoe

import com.robert.db.dao.shoe.ShoeDao
import com.robert.models.PaginatedShoes
import com.robert.models.Shoe
import com.robert.utils.BaseResponse
import io.ktor.http.*

class ShoeRepositoryImpl(
    private val shoeDao: ShoeDao
) : ShoeRepository {

    override suspend fun insertShoe(shoe: Shoe): BaseResponse<Shoe> {
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

    override suspend fun getShoeById(id: Int): BaseResponse<Shoe> {
        return try {
            val shoe = shoeDao.getShoeById(id)
            if (shoe != null) {
                BaseResponse.SuccessResponse(shoe)
            } else {
                BaseResponse.ErrorResponse("Shoe not found", HttpStatusCode.NotFound)
            }
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getAllShoesPaginated(page: Int, pageSize: Int): BaseResponse<PaginatedShoes> {
        return try {
            val paginatedShoes = shoeDao.getAllShoesPaginated(page, pageSize)
            BaseResponse.SuccessResponse(data = paginatedShoes)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun filterShoesByBrand(brand: String): BaseResponse<List<Shoe>> {
        return try {
            val shoes = shoeDao.filterShoesByBrand(brand)
            BaseResponse.SuccessResponse(shoes)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun filterShoesByCategory(category: String): BaseResponse<List<Shoe>> {
        return try {
            val shoes = shoeDao.filterShoesByCategory(category)
            BaseResponse.SuccessResponse(shoes)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse("An error occurred: ${e.message}", HttpStatusCode.InternalServerError)
        }
    }


    override suspend fun updateShoe(shoe: Shoe): BaseResponse<Shoe> {
        // TODO: Implement update shoe logic
        return BaseResponse.ErrorResponse("Not implemented yet", HttpStatusCode.NotImplemented)
    }

    override suspend fun deleteShoe(id: Int): BaseResponse<Shoe> {
        // TODO: Implement delete shoe logic
        return BaseResponse.ErrorResponse("Not implemented yet", HttpStatusCode.NotImplemented)
    }
}