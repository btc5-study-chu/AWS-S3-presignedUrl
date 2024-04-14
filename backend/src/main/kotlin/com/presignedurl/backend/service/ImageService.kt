package com.presignedurl.backend.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.presignedurl.backend.config.S3Config
import com.presignedurl.backend.entity.ImageEntity
import com.presignedurl.backend.model.FileNameContentType
import com.presignedurl.backend.model.PresignedUrl
import com.presignedurl.backend.model.RequestGetPresinged
import com.presignedurl.backend.model.ResponseImage
import com.presignedurl.backend.repository.ImageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

class UnsupportedContentTypeException(message: String) : Exception(message)
interface ImageService {
    fun createPutPresignedUrls (files:List<FileNameContentType>):List<PresignedUrl>
    fun createGetPresignedUrls (getDataReq:List<RequestGetPresinged>):List<PresignedUrl>
    fun getAllImages():List<ResponseImage>
}

@Service
class ImageServiceImpl(
    private val amazonS3: AmazonS3,
    @Autowired private val s3Config: S3Config,
    val repository:ImageRepository
):ImageService {
    override fun createPutPresignedUrls(files:List<FileNameContentType>):List<PresignedUrl> {
        val urls = files.map { fileNameAndContentType ->
            val extension = getExtension(fileNameAndContentType.contentType)
            val fileName = "${UUID.randomUUID()}.$extension" //階層をつけたい場合は、この先頭に/で区切って階層をつける 例） /test/folder/ファイル名
            repository.save(
                ImageEntity(
                    fileActualName = fileNameAndContentType.fileName,
                    fileUUIDName = fileName
                )
            )
            PresignedUrl(
                fileName,
                createPresignedUrl(fileName)
            )
        }
        return urls
    }

    override fun createGetPresignedUrls(getDataReq: List<RequestGetPresinged>): List<PresignedUrl> {
        val urls = getDataReq.map {
            val uuid = UUID.fromString(it.id)
            val res = repository.findById(uuid).orElseThrow {
                NoSuchElementException("Image with ID $uuid not found")
            }
            val fileName = res.fileUUIDName
            PresignedUrl(
                fileName,
                createGetPresignedUrl(fileName)
            )
        }
        return urls
    }

    override fun getAllImages():List<ResponseImage> {
        val res = repository.findAll()
        return res.map {
            ResponseImage(
                id = it.id!!,
                fileName = it.fileActualName
            )
        }
    }

    private fun getExtension(contentType: String): String {
        return when (contentType) {
            "image/jpeg" -> "jpeg"
            "image/png" -> "png"
            "image/heif" -> "heif"
            "image/heic" -> "heic"
            else -> {
                throw UnsupportedContentTypeException("'$contentType' is not a supported image type.")
            }
        }
    }

    private fun createPresignedUrl(fileName: String): String {
        return amazonS3.generatePresignedUrl(
            s3Config.bucket,
            fileName,
            get24hoursExpiration(),
            HttpMethod.PUT
        ).toString()
    }
    private fun createGetPresignedUrl(fileName: String): String {
        return amazonS3.generatePresignedUrl(
            s3Config.bucket,
            fileName,
            get24hoursExpiration(),
            HttpMethod.GET
        ).toString()
    }

    private fun get24hoursExpiration(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DATE, 1)

        return calendar.time
    }
}