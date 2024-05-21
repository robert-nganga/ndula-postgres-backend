package com.robert.db.dao.brand

import com.robert.models.Brand
import com.robert.models.Shoe

interface BrandDao {

    suspend fun insertBrand(brand: Brand): Brand?
    suspend fun searchShoes(brand: String, query: String): List<Shoe>
    suspend fun getBrandId(name: String): Int?
    suspend fun getAllBrands(): List<Brand>
}