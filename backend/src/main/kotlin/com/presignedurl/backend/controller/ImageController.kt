package com.presignedurl.backend.controller

import com.presignedurl.backend.model.PresignedUrl
import com.presignedurl.backend.model.PresignedUrlsRequest
import com.presignedurl.backend.service.ImageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/images")
class ImageController (
    val imageService: ImageService
) {

    @GetMapping("/getPresignedUrls")
    fun createGetPresignedUrls (){}

    @PostMapping("/putPresignedUrls")
    fun createPutPresignedUrls (
        @RequestBody request:PresignedUrlsRequest
    ):List<PresignedUrl> {
        return imageService.createPutPresignedUrls(request.files)
    }
}