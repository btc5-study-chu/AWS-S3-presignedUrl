package com.presignedurl.backend.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table
data class ImageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    val fileActualName: String = "",
    val fileUUIDName: String = "",
)



