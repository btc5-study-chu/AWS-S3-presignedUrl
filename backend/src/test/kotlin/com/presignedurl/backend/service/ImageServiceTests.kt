package com.presignedurl.backend.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.presignedurl.backend.config.S3Config
import com.presignedurl.backend.model.FileNameContentType
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URL
import java.util.*

class ImageServiceTests {
    private val mockedS3 = mockk<AmazonS3>()
    private val mockedS3Config = mockk<S3Config>()

    @Test
    fun `createPresignedUrls()を実行すると、S3からpresignedUrlsを作成して、それを返す`() {
        // arrange
        every { mockedS3Config.bucket } returns "my-bucket"
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val id3 = UUID.randomUUID()
        mockkStatic(UUID::class)
        every { UUID.randomUUID() } returnsMany listOf(id1, id2, id3)
        val url1 = "/$id1.jpeg"
        val url2 = "/$id2.jpeg"
        val url3 = "/$id3.png"
        every { mockedS3.generatePresignedUrl(any(), any(), any(), any()) } returnsMany listOf(
            URL("http", "www.example.com", url1),
            URL("http", "www.example.com", url2),
            URL("http", "www.example.com", url3)
        )
        val service = ImageServiceImpl(mockedS3, mockedS3Config)

        // action
        val result = service.createPresignedUrls(
            listOf(
                FileNameContentType("image/jpeg", "original1.jpeg"),
                FileNameContentType("image/jpeg", "original2.jpg"),
                FileNameContentType("image/png", "original3.png")
            )
        )

        verifySequence {
            mockedS3.generatePresignedUrl(
                "my-bucket",
                "$id1.jpeg",
                any(),
                HttpMethod.PUT
            )
            mockedS3.generatePresignedUrl(
                "my-bucket",
                "$id2.jpeg",
                any(),
                HttpMethod.PUT
            )
            mockedS3.generatePresignedUrl(
                "my-bucket",
                "$id3.png",
                any(),
                HttpMethod.PUT
            )
        }

        assertEquals("http://www.example.com$url1", result[0].url)
        assertEquals("http://www.example.com$url2", result[1].url)
        assertEquals("http://www.example.com$url3", result[2].url)
        unmockkStatic(UUID::class)
    }
}