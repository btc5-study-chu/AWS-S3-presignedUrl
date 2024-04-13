package com.presignedurl.backend.model

import java.util.UUID

data class ResponseImage(
    val id:UUID = UUID.randomUUID(),
    val fileName:String = ""
)
