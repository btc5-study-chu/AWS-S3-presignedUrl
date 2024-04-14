package com.presignedurl.backend.model.response

data class ResponsePresignedUrl(
    val fileName: String,
    val url: String
)