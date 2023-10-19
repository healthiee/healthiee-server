package healthiee.rest.lib.uploader

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.CommonErrorCode.INTERNAL_SERVER_ERROR
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Component
class S3Uploader(
    private val s3Client: AmazonS3Client,
    @Value("\${cloud.aws.bucket}")
    private val bucket: String,
) {

    fun upload(multipartFile: MultipartFile, mediaType: MediaType, domainType: MediaDomainType): String {
        val uploadFile: File = convert(multipartFile)
            ?: throw ApiException(INTERNAL_SERVER_ERROR)
        return upload(uploadFile, mediaType, domainType)
    }

    private fun upload(uploadFile: File, mediaType: MediaType, domainType: MediaDomainType): String {
        val fileName = "${mediaType.dirName}/${domainType.prefix}_${UUID.randomUUID()}"
        val uploadImageUrl = putS3(uploadFile, fileName)

        removeNewFile(uploadFile)

        return uploadImageUrl
    }

    private fun putS3(uploadFile: File, fileName: String): String {
        s3Client.putObject(
            PutObjectRequest(
                bucket,
                fileName,
                uploadFile,
            ).withCannedAcl(CannedAccessControlList.PublicRead)
        )
        return s3Client.getUrl(bucket, fileName).toString()
    }

    private fun removeNewFile(targetFile: File) {
        if (targetFile.delete()) {
            println("임시 파일 삭제 완료")
        } else {
            println("임시 파일 삭제 실패")
        }
    }

    private fun convert(file: MultipartFile): File? {
        val convertFile = File(file.originalFilename ?: return null)
        if (convertFile.createNewFile()) {
            val fos = FileOutputStream(convertFile)
            fos.write(file.bytes)
            return convertFile
        }
        return null
    }

}