package com.presignedurl.backend.controller

import com.presignedurl.backend.model.response.ResponsePresignedUrl
import com.presignedurl.backend.model.request.RequestPutPresignedUrls
import com.presignedurl.backend.model.request.RequestGetPresinged
import com.presignedurl.backend.model.response.ResponseImage
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
    @GetMapping
    fun getAllImages ():List<ResponseImage> {
        return imageService.getAllImages()
    }

    @PostMapping("/getPresignedUrls")
    fun createGetPresignedUrls (
        @RequestBody request: List<RequestGetPresinged>
    ):List<ResponsePresignedUrl>{
        return imageService.createGetPresignedUrls(request)
    }

    @PostMapping("/putPresignedUrls")
    fun createPutPresignedUrls (
        @RequestBody request: RequestPutPresignedUrls
    ):List<ResponsePresignedUrl> {
        return imageService.createPutPresignedUrls(request)
    }
}