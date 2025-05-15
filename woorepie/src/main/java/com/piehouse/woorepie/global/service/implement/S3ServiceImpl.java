package com.piehouse.woorepie.global.service.implement;

import com.piehouse.woorepie.global.dto.response.S3UrlResponse;
import com.piehouse.woorepie.global.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class S3ServiceImpl implements S3Service {

    private final S3Presigner presigner;
    private final String bucketName;
    private final String region;

    private final Duration validFor = Duration.ofMinutes(5);

    public S3ServiceImpl(S3Presigner presigner, @Value("${aws.s3.bucket}") String bucketName, @Value("${aws.region}") String region) {
        this.presigner = presigner;
        this.bucketName = bucketName;
        this.region = region;
    }

    @Override
    public String getPublicS3Url(String key) {
        System.out.println(String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region,
                key
        ));
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region,
                key
        );
    }

    @Override
    public S3UrlResponse generateCustomerPresignedUrl(String domain, String customerEmail) {

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String objectKey = String.format("%s/%s/%s", domain+"/identification", customerEmail, timestamp);

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(p -> p
                .signatureDuration(validFor)
                .putObjectRequest(putReq)
        );

        URL url = presignedRequest.url();

        return S3UrlResponse.builder()
                .url(url.toString())
                .key(objectKey)
                .expiresIn(validFor.getSeconds())
                .build();

    }

    @Override
    public List<S3UrlResponse> generateAgentPresignedUrl(String domain, String agentEmail) {

        String[] files = {"identification", "cert", "warrant"};
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        return Arrays.stream(files)
                .map(fileType -> {
                    String objectKey = String.format("%s/%s/%s-%s", domain, agentEmail, fileType, timestamp);

                    PutObjectRequest putReq = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build();

                    PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(p -> p
                            .signatureDuration(validFor)
                            .putObjectRequest(putReq)
                    );

                    return S3UrlResponse.builder()
                            .url(presignedRequest.url().toString())
                            .key(objectKey)
                            .expiresIn(validFor.getSeconds())
                            .build();
                })
                .toList();

    }

    @Override
    public List<S3UrlResponse> generateEstatePresignedUrl(String domain, String estateAddress) {

        String[] files = {"estate-image", "sub-guide", "securities-report", "investment-explanation",
        "property-mng-contract", "appraisal-report"};
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        return Arrays.stream(files)
                .map(fileType -> {
                    String objectKey = String.format("%s/%s/%s-%s", domain, estateAddress, fileType, timestamp);

                    PutObjectRequest putReq = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build();

                    PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(p -> p
                            .signatureDuration(validFor)
                            .putObjectRequest(putReq)
                    );

                    return S3UrlResponse.builder()
                            .url(presignedRequest.url().toString())
                            .key(objectKey)
                            .expiresIn(validFor.getSeconds())
                            .build();
                })
                .toList();

    }

}
