package com.robert.repositories.images

import com.robert.utils.BaseResponse

interface ImageRepository {

    suspend fun uploadImage(bytes: ByteArray, fileName: String): BaseResponse<String>
}