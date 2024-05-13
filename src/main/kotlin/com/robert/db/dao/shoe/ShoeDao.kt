package com.robert.db.dao.shoe

import com.robert.models.Brand
import com.robert.models.Shoe

interface ShoeDao {

    suspend fun insertShoe(shoe: Shoe): Shoe?

    suspend fun getShoeById(id: Int): Shoe?

    suspend fun getShoesByBrand(brand: String): List<Shoe>

    suspend fun updateShoe(shoe: Shoe): Shoe?

    suspend fun deleteShoe(shoe: Shoe): Boolean

}