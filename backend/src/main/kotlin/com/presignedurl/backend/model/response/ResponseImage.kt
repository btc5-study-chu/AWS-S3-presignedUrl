package com.presignedurl.backend.model.response

import java.util.UUID

data class ResponseImage(
    val id:UUID = UUID.randomUUID(),
    val fileName:String = ""
)
