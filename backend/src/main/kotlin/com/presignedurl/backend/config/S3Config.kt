package com.presignedurl.backend.config

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class S3Config(
    @Value("\${aws.s3.endpoint}")
    private val endpoint: String,

    @Value("\${aws.s3.region}")
    private val region: String,

    @Value("\${aws.s3.use-minio}")
    private val useMinIO: Boolean,

    @Value("\${aws.s3.bucket}")
    val bucket: String
) {
    @Bean
    fun baseS3Url(): String {
        return if (useMinIO) {
            "$endpoint/$bucket"
        } else {
            "https://$bucket.s3.$region.amazonaws.com"
        }
    }

    @Bean
    fun amazonS3(): AmazonS3 {
        val endpointConfiguration = AwsClientBuilder.EndpointConfiguration(
            endpoint,
            region
        )
        val clientConfiguration = ClientConfiguration()
        clientConfiguration.connectionTimeout = 10000
        clientConfiguration.requestTimeout = 10000

        val clientBuilder = AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(endpointConfiguration)
            .withPathStyleAccessEnabled(true)
            .withClientConfiguration(clientConfiguration)

        if (useMinIO) {
            clientBuilder.withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials("root", "password")))
        }

        return clientBuilder.build()
    }
}
