package com.presignedurl.backend.model

data class PresignedUrlsRequest(
    val files: List<FileNameContentType>
)

data class FileNameContentType(
    val contentType: String,
    val fileName: String
)