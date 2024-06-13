package com.robert.repositories.images

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import com.robert.utils.BaseResponse

class ImageRepositoryImpl(
    private val s3Client: S3Client
): ImageRepository {
    private val bucketName = "ndulaimages"

    override suspend fun uploadImage(bytes: ByteArray, fileName: String): BaseResponse<String> {
        return try {
            println(fileName)
            val putObjectRequest = PutObjectRequest{
                bucket = bucketName
                key = fileName
                body = ByteStream.fromBytes(bytes = bytes)
            }
            s3Client.putObject(putObjectRequest)
            val imageUrl = "https://${bucketName}.s3.amazonaws.com/$fileName"
            BaseResponse.SuccessResponse(imageUrl)
        } catch (e: Exception) {
            BaseResponse.ErrorResponse(e.message ?: "Error uploading image")
        }
    }
}