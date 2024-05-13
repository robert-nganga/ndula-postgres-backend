package com.robert.db.dao.brand

import com.robert.models.Brand

interface BrandDao {

    suspend fun insertBrand(brand: Brand): Brand?
    suspend fun getBrandById(id: Int): Brand?
    suspend fun getAllBrands(): List<Brand>
}