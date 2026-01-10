package org.csps.backend.service;


import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private final String bucketName;
    private final S3Client s3Client;



    public S3Service(
            @Value("${AWS_ACCESS_KEY_ID}") String accessKeyId,
            @Value("${AWS_SECRET_ACCESS_KEY}") String secretAccessKey,
            @Value("${AWS_REGION}") String region,
            @Value("${S3_BUCKET_NAME:}") String bucketName) {
        
        this.bucketName = bucketName;
        
        if (accessKeyId == null || accessKeyId.isEmpty() || secretAccessKey == null || secretAccessKey.isEmpty() || bucketName == null || bucketName.isEmpty()) {
            throw new IllegalArgumentException("AWS credentials and bucket name must be configured in application.properties");
        }
        
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    /**
     * Upload file to S3
     */
    public String uploadFile(MultipartFile file, String stakeHolderId) throws IOException {
        String fileName = generateFileName(stakeHolderId, file.getOriginalFilename());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .contentDisposition("inline")   // <<< makes browser view instead of download
                .build();

        s3Client.putObject(putObjectRequest,
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

        return fileName;
    }


    /**
     * Download file from S3
     */
    public byte[] downloadFile(String fileName) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return s3Client.getObject(getObjectRequest).readAllBytes();
    }

    /**
     * Delete file from S3
     */
    public void deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    /**
     * Generate unique file name
     */
    private String generateFileName(String stakeHolderId, String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return "documents/" + stakeHolderId + "/" + UUID.randomUUID() + extension;
    }
}
