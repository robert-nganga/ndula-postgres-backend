package com.robert.db.dao.shoe

import com.robert.models.PaginatedShoes
import com.robert.models.Shoe
import com.robert.request.ShoeRequest
import org.jetbrains.exposed.sql.ResultRow

interface ShoeDao {
    fun resultRowToShoe(row: ResultRow): Shoe

    suspend fun insertShoe(shoe: ShoeRequest): Shoe?

    suspend fun searchShoes(query: String): List<Shoe>

    suspend fun getShoeById(id: Int): Shoe

    suspend fun getAllShoesPaginated(page: Int, pageSize: Int): PaginatedShoes

    suspend fun filterShoesByBrand(brand: String): List<Shoe>

    suspend fun filterShoesByCategory(category: String): List<Shoe>

    suspend fun updateShoe(shoe: Shoe): Shoe?

    suspend fun deleteShoe(shoeId: Int): Boolean

}