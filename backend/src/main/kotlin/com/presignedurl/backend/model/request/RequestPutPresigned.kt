package com.presignedurl.backend.model.request

data class RequestPutPresignedUrls(
    val files: List<FileNameContentType>
)

data class FileNameContentType(
    val contentType: String,
    val fileName: String
)