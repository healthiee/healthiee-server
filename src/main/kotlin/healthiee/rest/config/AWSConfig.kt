package healthiee.rest.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSSessionCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.InstanceProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AWSConfig(
    @Value("\${cloud.aws.access-key}")
    private val accessKey: String,
    @Value("\${cloud.aws.secret-key}")
    private val secretKey: String,
) {

    @Bean
    fun credentialsProvider(): AWSStaticCredentialsProvider =
        AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey))

    @Bean
    fun amazonS3Client(): AmazonS3Client {
        val s3Client = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.AP_NORTHEAST_2)
            .withCredentials(credentialsProvider())
            .build()

        return s3Client as AmazonS3Client
    }

    @Bean
    fun amazonSimpleEmailService(): AmazonSimpleEmailService = AmazonSimpleEmailServiceClientBuilder
        .standard()
        .withCredentials(credentialsProvider())
        .withRegion(Regions.AP_NORTHEAST_2)
        .build()

}