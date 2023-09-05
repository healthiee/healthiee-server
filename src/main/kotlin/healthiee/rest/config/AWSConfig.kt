package healthiee.rest.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AWSConfig(
    @Value("\${aws.access-key}")
    private val accessKey: String,
    @Value("\${aws.secret-key}")
    private val secretKey: String,
) {

    @Bean
    fun provideSES(
        credentialsProvider: AWSStaticCredentialsProvider,
    ): AmazonSimpleEmailService = AmazonSimpleEmailServiceClientBuilder
        .standard()
        .withCredentials(credentialsProvider)
        .withRegion(Regions.AP_NORTHEAST_2)
        .build()

    @Bean
    fun provideCredentials(): AWSStaticCredentialsProvider =
        AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey))

}