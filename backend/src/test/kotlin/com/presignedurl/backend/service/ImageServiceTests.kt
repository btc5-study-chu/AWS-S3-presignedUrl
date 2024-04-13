package com.presignedurl.backend.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.presignedurl.backend.config.S3Config
import com.presignedurl.backend.entity.ImageEntity
import com.presignedurl.backend.model.FileNameContentType
import com.presignedurl.backend.repository.ImageRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import java.net.URL
import java.util.*

class ImageServiceTests {
    private val mockedS3 = mockk<AmazonS3>()
    private val mockedS3Config = mockk<S3Config>()

    @MockBean
    private lateinit var repository:ImageRepository

    private lateinit var service: ImageService


    @BeforeEach
    fun setUp () {
        repository = mockk()
        service = ImageServiceImpl(mockedS3,mockedS3Config, repository)
    }

    @Nested
    inner class `getAllImagesのテスト`{
        @Test
        fun `getAllImagesを呼ぶとrepositoryのfindAll()を呼び正しい値を返す`(){
            every { repository.findAll() } returns listOf(
                ImageEntity(id = UUID.randomUUID(), fileActualName = "testImage1", fileUUIDName = "testUUID1"),
                ImageEntity(id = UUID.randomUUID(),fileActualName = "testImage2", fileUUIDName = "testUUID2"),
                ImageEntity(id = UUID.randomUUID(),fileActualName = "testImage3", fileUUIDName = "testUUID3"),
            )

            val res = service.getAllImages()

            verify { repository.findAll() }

            assertEquals("testImage1",res[0].fileName)
            assertEquals("testImage2",res[1].fileName)
            assertEquals("testImage3",res[2].fileName)
        }
    }

    @Nested
    inner class `createPutPresignedUrlsのテスト` {
        @Test
        fun `createPutPresignedUrls()を実行すると、正しい引数でImageRepositorysaveメソッドを呼ぶ`() {
            // arrange
            every { mockedS3Config.bucket } returns "my-bucket"
            val id1 = UUID.randomUUID()
            val id2 = UUID.randomUUID()
            val id3 = UUID.randomUUID()
            mockkStatic(UUID::class)
            every { UUID.randomUUID() } returnsMany listOf(id1, id2, id3)

            every { mockedS3.generatePresignedUrl(any(), any(), any(), any()) } returnsMany listOf(
                URL("http", "www.example.com", ""),
                URL("http", "www.example.com",""),
                URL("http", "www.example.com", "")
            )
            every { repository.save(any()) } returns ImageEntity()

            // action
            val result = service.createPutPresignedUrls(
                listOf(
                    FileNameContentType("image/jpeg", "original1.jpeg"),
                    FileNameContentType("image/jpeg", "original2.jpg"),
                    FileNameContentType("image/png", "original3.png")
                )
            )

            verifySequence {
                repository.save(
                    match {
                        it.fileActualName == "original1.jpeg" &&
                        it.fileUUIDName == "$id1.jpeg"
                    }
                )
                repository.save(
                    match {
                        it.fileActualName == "original2.jpg" &&
                        it.fileUUIDName == "$id2.jpeg" //jpg拡張子はjpegで扱われるため
                    }
                )
                repository.save(
                    match {
                        it.fileActualName == "original3.png" &&
                        it.fileUUIDName == "$id3.png"
                    }
                )
            }
            unmockkStatic(UUID::class)
        }

        @Test
        fun `createPutPresignedUrls()を実行すると、S3からpresignedUrlsを作成して、それを返す`() {
            // arrange
            every { mockedS3Config.bucket } returns "my-bucket"
            val id1 = UUID.randomUUID()
            val id2 = UUID.randomUUID()
            val id3 = UUID.randomUUID()
            val id4 = UUID.randomUUID()
            mockkStatic(UUID::class)
            every { UUID.randomUUID() } returnsMany listOf(id1,id2,id3)
            val url1 = "/$id1.jpeg"
            val url2 = "/$id2.jpeg"
            val url3 = "/$id3.png"
            every { mockedS3.generatePresignedUrl(any(), any(), any(), any()) } returnsMany listOf(
                URL("http", "www.example.com", url1),
                URL("http", "www.example.com", url2),
                URL("http", "www.example.com", url3)
            )
            every { repository.save(any()) } returns ImageEntity()

            // action
            val result = service.createPutPresignedUrls(
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
}