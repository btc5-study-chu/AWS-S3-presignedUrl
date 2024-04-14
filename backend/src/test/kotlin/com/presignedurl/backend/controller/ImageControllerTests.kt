package com.presignedurl.backend.controller

import com.presignedurl.backend.model.request.FileNameContentType
import com.presignedurl.backend.model.request.RequestGetPresinged
import com.presignedurl.backend.model.request.RequestPutPresignedUrls
import com.presignedurl.backend.model.response.ResponsePresignedUrl
import com.presignedurl.backend.model.response.ResponseImage
import com.presignedurl.backend.service.ImageService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
//@AutoConfigureMockRestServiceServer
@AutoConfigureMockMvc
class ImageControllerTests{

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: ImageService

    private lateinit var controller:ImageController

    @BeforeEach
    fun setUp () {
        service = mockk()
        controller = ImageController(service)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Nested
    inner class `getAllImagesのテスト` {
        @Test
        fun `getAllImagesを呼ぶと 200 OK でかつservice層のgetAllImagesを呼んで正しい値を返す` () {
            every { service.getAllImages() } returns listOf(
                ResponseImage(fileName = "testImage1"),
                ResponseImage(fileName = "testImage2")
            )

            val res = mockMvc.perform(
                get("/api/images")
            )
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fileName").value("testImage1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].fileName").value("testImage2"))

            verify (exactly = 1) { service.getAllImages() }
        }
    }

    @Nested
    inner class `createGetPresignedUrlsのテスト` {
        @Test
        fun `api_images_getPresignedUrlsを呼ぶと200OKでかつ正しい引数でservice層のcreateGetPresignedUrlsを呼ぶ`() {
            every { service.createGetPresignedUrls(any()) } returns emptyList()

            mockMvc.perform(
                post("/api/images/getPresignedUrls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                            [
                                {"id": "test1"},
                                {"id": "test2"},
                                {"id": "test3"}
                            ]
                        """
                    )
            )
                .andExpect(status().isOk)

            verify { service.createGetPresignedUrls(
                listOf(
                    RequestGetPresinged(id="test1"),
                    RequestGetPresinged(id="test2"),
                    RequestGetPresinged(id="test3")
                )
            ) }
        }

        @Test
        fun `api_images_getPresignedUrlsを呼ぶと正しい値を返す`() {
            every { service.createGetPresignedUrls(any()) } returns listOf(
                ResponsePresignedUrl(fileName = "testFile1", url = "testUrl1"),
                ResponsePresignedUrl(fileName = "testFile2", url = "testUrl2"),
                ResponsePresignedUrl(fileName = "testFile3", url = "testUrl3"),
            )

            mockMvc.perform(
                post("/api/images/getPresignedUrls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                            [
                                {"id": "dummy"},
                                {"id": "dummy"},
                                {"id": "dummy"}
                            ]
                        """
                    )
            )
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fileName").value("testFile1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].url").value("testUrl1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].fileName").value("testFile2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].url").value("testUrl2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].fileName").value("testFile3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].url").value("testUrl3"))
        }
    }

    @Nested
    inner class createPutResponsePresignedUrlsのテスト {
        @Test
        fun `createPutPresignedUrls() を呼んだ時、200 OKでかつ正しい引数でサービス層のcreatePresignedUrlメソッドを呼ぶ`() {
            every { service.createPutPresignedUrls(any()) } returns listOf(
                ResponsePresignedUrl("test1","https://test1"),
                ResponsePresignedUrl("test2","https://test2"),
                ResponsePresignedUrl("test3","https://test3"),
                ResponsePresignedUrl("test4","https://test4"),
                ResponsePresignedUrl("test5","https://test5"),
            )

            mockMvc.perform(
                post("/api/images/putPresignedUrls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                           {"files": [
                            {"contentType": "image/jpeg", "fileName": "original1.jpeg"},
                            {"contentType": "image/jpeg", "fileName": "original2.jpg"},
                            {"contentType": "image/png", "fileName": "original3.png"},
                            {"contentType": "image/heif", "fileName": "original4.heif"},
                            {"contentType": "image/heic", "fileName": "original5.heic"}
                           ]}
                            """.trimIndent()
                    )
            )
                .andExpect(status().isOk)

            verify (exactly = 1) { service.createPutPresignedUrls(
                RequestPutPresignedUrls(
                    files = listOf(
                        FileNameContentType("image/jpeg","original1.jpeg"),
                        FileNameContentType("image/jpeg","original2.jpg"),
                        FileNameContentType("image/png","original3.png"),
                        FileNameContentType("image/heif","original4.heif"),
                        FileNameContentType("image/heic","original5.heic")
                    )
                )
            ) }
        }

        @Test
        fun `createPutPresignedUrls() を呼んだ時、正しい返り値を返す`() {
            every { service.createPutPresignedUrls(any()) } returns listOf(
                ResponsePresignedUrl("test1","https://test1"),
                ResponsePresignedUrl("test2","https://test2"),
                ResponsePresignedUrl("test3","https://test3"),
                ResponsePresignedUrl("test4","https://test4"),
                ResponsePresignedUrl("test5","https://test5"),
            )

            mockMvc.perform(
                post("/api/images/putPresignedUrls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                           {"files": [
                            {"contentType": "image/jpeg", "fileName": "original1.jpeg"},
                            {"contentType": "image/jpeg", "fileName": "original2.jpg"},
                            {"contentType": "image/png", "fileName": "original3.png"},
                            {"contentType": "image/heif", "fileName": "original4.heif"},
                            {"contentType": "image/heic", "fileName": "original5.heic"}
                           ]}
                            """.trimIndent()
                    )
            )
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fileName").value("test1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].url").value("https://test1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].fileName").value("test2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].url").value("https://test2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].fileName").value("test3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].url").value("https://test3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[3].fileName").value("test4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[3].url").value("https://test4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[4].fileName").value("test5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[4].url").value("https://test5"))
        }
    }



}