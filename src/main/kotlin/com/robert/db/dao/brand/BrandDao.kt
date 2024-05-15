package com.robert.db.dao.brand

import com.robert.models.Brand

interface BrandDao {

    suspend fun insertBrand(brand: Brand): Brand?
    suspend fun getBrandId(name: String): Int?
    suspend fun getAllBrands(): List<Brand>
}