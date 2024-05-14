package com.robert.repositories.shoe

import com.robert.models.Brand
import com.robert.utils.BaseResponse

interface BrandRepository {
    suspend fun insertBrand(brand: Brand): BaseResponse<Brand>
    suspend fun getAllBrands(): BaseResponse<List<Brand>>
}