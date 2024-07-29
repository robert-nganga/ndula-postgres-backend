package com.robert.repositories.shoe

import com.robert.db.dao.brand.BrandDao
import com.robert.models.Brand
import com.robert.models.Shoe
import com.robert.utils.BaseResponse
import io.ktor.http.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

class BrandRepositoryImpl(
    private  val brandDao: BrandDao
): BrandRepository {

    override suspend fun insertBrand(brand: Brand): BaseResponse<Brand> {
        return  try {
            val result = brandDao.insertBrand(brand)
            if (result == null) {
                BaseResponse.ErrorResponse(message = "Error while inserting brand", status = HttpStatusCode.InternalServerError)
            } else{
                BaseResponse.SuccessResponse(data = result,  status = HttpStatusCode.Created)
            }
        } catch (e: ExposedSQLException){
            BaseResponse.ErrorResponse(message = "Brand name already exists", status = HttpStatusCode.InternalServerError)
        } catch (e: Exception){
            BaseResponse.ErrorResponse(message = "Error adding brand", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun searchShoes(brand: String, query: String, userId: Int?): BaseResponse<List<Shoe>> {
        return  try {
            val result = brandDao.searchShoes(brand, query, userId)
            BaseResponse.SuccessResponse(data = result, status = HttpStatusCode.OK)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(message = "Error fetching shoes", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getAllBrands(): BaseResponse<List<Brand>> {
        return try {
            val result = brandDao.getAllBrands()
            BaseResponse.SuccessResponse(data = result, status = HttpStatusCode.OK)
        } catch (e: Exception){
            BaseResponse.ErrorResponse(message = "Error fetching brands", status = HttpStatusCode.InternalServerError)
        }
    }
}