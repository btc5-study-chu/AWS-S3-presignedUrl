package com.presignedurl.backend.repository

import com.presignedurl.backend.entity.ImageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ImageRepository:JpaRepository<ImageEntity,UUID> {
}