package com.presignedurl.backend.controller

import com.presignedurl.backend.model.FileNameContentType
import com.presignedurl.backend.model.PresignedUrl
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
    inner class `createPutPresignedUrlsのテスト` {
        @Test
        fun `createPutPresignedUrls() を呼んだ時、200 OKでかつ正しい引数でサービス層のcreatePresignedUrlメソッドを呼ぶ`() {
            every { service.createPutPresignedUrls(any()) } returns listOf(
                PresignedUrl("test1","https://test1"),
                PresignedUrl("test2","https://test2"),
                PresignedUrl("test3","https://test3"),
                PresignedUrl("test4","https://test4"),
                PresignedUrl("test5","https://test5"),
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
                listOf(
                    FileNameContentType("image/jpeg","original1.jpeg"),
                    FileNameContentType("image/jpeg","original2.jpg"),
                    FileNameContentType("image/png","original3.png"),
                    FileNameContentType("image/heif","original4.heif"),
                    FileNameContentType("image/heic","original5.heic")
                )
            ) }
        }

        @Test
        fun `createPutPresignedUrls() を呼んだ時、正しい返り値を返す`() {
            every { service.createPutPresignedUrls(any()) } returns listOf(
                PresignedUrl("test1","https://test1"),
                PresignedUrl("test2","https://test2"),
                PresignedUrl("test3","https://test3"),
                PresignedUrl("test4","https://test4"),
                PresignedUrl("test5","https://test5"),
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

    @Nested
    inner class `createGetPresignedUrlsのテスト` {
        @Test
        fun `api_images_getPresignedUrlsを呼ぶと200OKになる`() {
            mockMvc.perform(
                get("/api/images/getPresignedUrls")
            )
                .andExpect(status().isOk)

        }
    }

}