package com.robert.repositories.shoe

import com.robert.models.PaginatedShoes
import com.robert.models.Shoe
import com.robert.request.ShoeRequest
import com.robert.utils.BaseResponse

interface ShoeRepository {

    suspend fun insertShoe(shoe: ShoeRequest): BaseResponse<Shoe>

    suspend fun getShoeById(id: Int, userId: Int?): BaseResponse<Shoe>

    suspend fun searchShoes(query: String, userId: Int?): BaseResponse<List<Shoe>>

    suspend fun getAllShoesPaginated(page: Int, pageSize: Int, userId: Int?): BaseResponse<PaginatedShoes>

    suspend fun filterShoesByBrand(brand: String, userId: Int?): BaseResponse<List<Shoe>>

    suspend fun filterShoesByCategory(category: String, userId: Int?): BaseResponse<List<Shoe>>

    suspend fun updateShoe(shoe: Shoe): BaseResponse<Shoe>

    suspend fun deleteShoe(id: Int): BaseResponse<Shoe>
}