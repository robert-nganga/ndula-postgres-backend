package com.robert.repositories.shoe

import com.robert.models.PaginatedShoes
import com.robert.models.Shoe
import com.robert.utils.BaseResponse

interface ShoeRepository {

    suspend fun insertShoe(shoe: Shoe): BaseResponse<Shoe>

    suspend fun getShoeById(id: Int): BaseResponse<Shoe>

    suspend fun getAllShoesPaginated(page: Int, pageSize: Int): BaseResponse<PaginatedShoes>

    suspend fun filterShoesByBrand(brand: String): BaseResponse<List<Shoe>>

    suspend fun filterShoesByCategory(category: String): BaseResponse<List<Shoe>>

    suspend fun updateShoe(shoe: Shoe): BaseResponse<Shoe>

    suspend fun deleteShoe(id: Int): BaseResponse<Shoe>
}