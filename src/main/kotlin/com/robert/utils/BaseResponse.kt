package com.robert.utils

import io.ktor.http.*


sealed class BaseResponse<T>{


    data class SuccessResponse<T>(
        val data: T? = null,
        val message: String? = null,
        val status: HttpStatusCode = HttpStatusCode.OK
    ) : BaseResponse<T>()

    data class ErrorResponse<T>(
        val exception: T? = null,
        val message: String? = null,
        val status: HttpStatusCode = HttpStatusCode.InternalServerError
    ) : BaseResponse<T>()

}