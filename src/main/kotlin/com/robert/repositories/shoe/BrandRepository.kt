package com.robert.repositories.shoe

import com.robert.models.Brand
import com.robert.models.Shoe
import com.robert.utils.BaseResponse

interface BrandRepository {
    suspend fun insertBrand(brand: Brand): BaseResponse<Brand>
    suspend fun searchShoes(brand: String, query: String, userId:Int?): BaseResponse<List<Shoe>>
    suspend fun getAllBrands(): BaseResponse<List<Brand>>
}