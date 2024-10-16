package com.cs7rishi.oFile.service.impl;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.entity.FileEntity;
import com.cs7rishi.oFile.repository.FileRepository;
import com.cs7rishi.oFile.service.DownloaderService;
import com.cs7rishi.oFile.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.io.ByteArrayOutputStream;
import static com.cs7rishi.oFile.utils.FileUtils.calculatePercentage;

/**
 * This service is responsible for downloading the file in local directory.
 */
@Service
public class DownloaderServiceImpl implements DownloaderService {
    @Value("${oFile.s3.bucketName}")
    private String bucketName;
    @Autowired
    private ExecutorService executorService;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private DownloadProgressCacheService progressCacheService;
    private final int MB = 1024 * 1024;
    private final int DOWNLOAD_COMPLETE = 100;

    @Override
    public void downloadFile(FileDto fileDto){
        String s3Key = FileUtils.createS3Key(fileDto.getId());
        progressCacheService.initiateProgress(fileDto.getId());
        executorService.submit(() -> {
            startDownload(fileDto, s3Key);
        });
    }

    /**
     * Delete file from s3 bucket
     * @param fileId File Id
     */
    @Override
    public void deleteFile(Long fileId) {
        progressCacheService.deleteFileProgress(fileId);
        //delete file from s3
        S3Client s3Client = S3Client.builder().build();
        String s3Key = FileUtils.createS3Key(fileId);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    private void startDownload(FileDto fileDto, String key) {
        try {
            S3Client s3Client = S3Client.builder().build(); // Create an S3 client
            // Initiate a multipart upload
            CreateMultipartUploadRequest createMultipartUploadRequest =
                CreateMultipartUploadRequest.builder().bucket(bucketName).key(key).build();
            CreateMultipartUploadResponse createMultipartUploadResponse =
                s3Client.createMultipartUpload(createMultipartUploadRequest);
            String uploadId = createMultipartUploadResponse.uploadId();

            ReadableByteChannel readChannel =
                Channels.newChannel(generateUrl(fileDto).openStream());
            ByteBuffer buffer = ByteBuffer.allocate(MB); // 1 MB buffer for reading
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            long totalBytesRead = 0;
            int partNumber = 1;
            List<CompletedPart> completedParts = new ArrayList<>();

            while (readChannel.read(buffer) != -1) {
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                baos.write(data);
                buffer.clear();

                totalBytesRead += data.length;
                updateDownloadProgress(fileDto, totalBytesRead);

                // If we've accumulated at least 5 MB, upload the part
                if (baos.size() >= 5 * MB) {
                    uploadPart(s3Client, bucketName, key, uploadId, partNumber, baos.toByteArray(),
                        completedParts);
                    partNumber++;
                    baos.reset();
                }
            }

            // Upload any remaining data as the final part
            if (baos.size() > 0) {
                uploadPart(s3Client, bucketName, key, uploadId, partNumber, baos.toByteArray(),
                    completedParts);
            }

            // Complete the multipart upload
            CompletedMultipartUpload completedMultipartUpload =
                CompletedMultipartUpload.builder().parts(completedParts).build();
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder().bucket(bucketName).key(key)
                    .uploadId(uploadId).multipartUpload(completedMultipartUpload).build();
            s3Client.completeMultipartUpload(completeMultipartUploadRequest);

            persistProgressInDB(fileDto);
            readChannel.close();
            setFileProgress(fileDto.getId(), DOWNLOAD_COMPLETE);
            System.out.println(
                "FileId: " + fileDto.getId() + " Download Completed and uploaded to S3");
        } catch (Exception ex) {
            System.out.println("Exception Occurred: " + ex.getMessage());
        }
    }

    public void setFileProgress(Long fileId, Integer progress) {
        Optional<FileEntity> fileEntity = fileRepository.findById(fileId);
        fileEntity.ifPresent(entity -> {
            entity.setProgress(progress);
            fileRepository.save(entity);
        });
    }

    private void uploadPart(S3Client s3Client, String bucketName, String key, String uploadId,
        int partNumber, byte[] data, List<CompletedPart> completedParts) {
        UploadPartRequest uploadPartRequest =
            UploadPartRequest.builder().bucket(bucketName).key(key).uploadId(uploadId)
                .partNumber(partNumber).build();
        UploadPartResponse uploadPartResponse =
            s3Client.uploadPart(uploadPartRequest, RequestBody.fromBytes(data));
        completedParts.add(
            CompletedPart.builder().partNumber(partNumber).eTag(uploadPartResponse.eTag()).build());
    }

    private void persistProgressInDB(FileDto fileDto) {
        Optional<FileEntity> fileEntity = fileRepository.findById(fileDto.getId());
        fileEntity.ifPresent((entity) -> {
            entity.setProgress(100);
            fileRepository.save(entity);
        });
    }

    private URL generateUrl(FileDto fileDto) throws MalformedURLException{
        return new URL(fileDto.getFileUrl());
    }


    private void updateDownloadProgress(FileDto fileDto, long bytesRead) {
        progressCacheService.setFileProgress(fileDto.getId(),
            calculatePercentage(bytesRead, fileDto.getFileSize()));
    }

}
