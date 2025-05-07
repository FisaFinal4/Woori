package com.piehouse.woorepie.global.service.impliment;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.global.dto.response.S3UrlResponse;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.global.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

    private final S3Presigner presigner;
    private final String bucketName;

    private final Duration validFor = Duration.ofMinutes(5);

    public S3ServiceImpl(S3Presigner presigner, @Value("${aws.s3.bucket}") String bucketName) {
        this.presigner = presigner;
        this.bucketName = bucketName;
    }

    @Override
    public S3UrlResponse generatePresignedUrl(SessionCustomer sessionCustomer) {

        if (sessionCustomer.getCustomerId()==null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        try {
            String originalFilename = sessionCustomer.getCustomerEmail();
            String uuid = UUID.randomUUID().toString();
            String objectKey = String.format("Customer/Identification/%s-%s", originalFilename, uuid);

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
        }catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

}
