package healthiee.rest.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class S3Config(
    @Value("\${cloud.aws.access-key}")
    private val accessKey: String,
    @Value("\${cloud.aws.secret-key}")
    private val secretKey: String,
) {

    @Bean
    fun s3Client(): AmazonS3Client {
        val credentials = BasicAWSCredentials(accessKey, secretKey)

        val s3Client = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.AP_NORTHEAST_2)
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .build()

        return s3Client as AmazonS3Client
    }


}