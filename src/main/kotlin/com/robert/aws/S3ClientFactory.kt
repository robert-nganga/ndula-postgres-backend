package com.robert.aws

import aws.sdk.kotlin.services.s3.S3Client

object S3ClientFactory {

    suspend fun init(): S3Client {
        return S3Client.fromEnvironment()
    }
}