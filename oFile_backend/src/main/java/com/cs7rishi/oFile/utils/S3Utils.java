package com.cs7rishi.oFile.utils;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

public class S3Utils {
    public static String createPresignedGetUrl(String bucketName, String keyName, Integer urlExpirationHour,String downloadFileName) {
        try (S3Presigner presigner = S3Presigner.create()) {
            GetObjectRequest objectRequest =
                GetObjectRequest.builder().bucket(bucketName).key(keyName).responseContentDisposition("attachment; filename=\"" + downloadFileName + "\"").build();
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(urlExpirationHour))  // The URL will expire in 10 minutes.
                .getObjectRequest(objectRequest).build();
            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toExternalForm();
        }
    }
}
