package com.cs7rishi.oFile.service.impl;

import com.cs7rishi.oFile.dto.FileDto;
import com.cs7rishi.oFile.entity.FileEntity;
import com.cs7rishi.oFile.exception.OFileException;
import com.cs7rishi.oFile.repository.FileRepository;
import com.cs7rishi.oFile.service.DownloaderService;
import com.cs7rishi.oFile.utils.AuthorizationUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
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
    @Autowired
    private ExecutorService executorService;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private DownloadProgressCacheService progressCacheService;
    private final String DOWNLOAD_DIR = "D:\\downloads";
    private final int BUFFER_SIZE = 1024;


    @PostConstruct
    private void createDownloadDirectory() {
        File file = new File(DOWNLOAD_DIR);
        System.out.println("Download directory created : " + file.mkdirs());
    }

    @Override
    public void downloadFile(FileDto fileDto) throws OFileException {
        File file = createFilePath(fileDto);
        System.out.println("File Path " + file.getAbsolutePath());
        progressCacheService.initiateProgress(fileDto.getId());
        executorService.submit(() -> {
            startDownload(fileDto, file);
        });
    }

    @Override
    public void deleteFile(Long fileId) {
        //Todo handle the interuption of thread, downloading this file
        progressCacheService.deleteFileProgress(fileId);

    }

    private void startDownload(FileDto fileDto, File file) {
        try {
            S3Client s3Client = S3Client.builder().build(); // Create an S3 client
            String bucketName = "ofile.cs7rishi.me";
            String key = file.getName();

            // Initiate a multipart upload
            CreateMultipartUploadRequest createMultipartUploadRequest =
                CreateMultipartUploadRequest.builder().bucket(bucketName).key(key).build();
            CreateMultipartUploadResponse createMultipartUploadResponse =
                s3Client.createMultipartUpload(createMultipartUploadRequest);
            String uploadId = createMultipartUploadResponse.uploadId();

            ReadableByteChannel readChannel =
                Channels.newChannel(generateUrl(fileDto).openStream());
            FileOutputStream outputStream = new FileOutputStream(file);
            FileChannel writeChannel = outputStream.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024); // 1 MB buffer for reading
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            long totalBytesRead = 0;
            int partNumber = 1;
            List<CompletedPart> completedParts = new ArrayList<>();

            while (readChannel.read(buffer) != -1) {
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                baos.write(data);
                writeChannel.write(ByteBuffer.wrap(data));
                buffer.clear();

                totalBytesRead += data.length;
                updateDownloadProgress(fileDto, totalBytesRead);

                // If we've accumulated at least 5 MB, upload the part
                if (baos.size() >= 5 * 1024 * 1024) {
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
            outputStream.close();
            readChannel.close();
            writeChannel.close();
            System.out.println(
                "FileId: " + fileDto.getId() + " Download Completed and uploaded to S3");
        } catch (Exception ex) {
            System.out.println("Exception Occurred: " + ex.getMessage());
        }
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

    private URL generateUrl(FileDto fileModel) throws MalformedURLException{
        return new URL(fileModel.getFileUrl());
    }

    private File createFilePath(FileDto fileDto) throws OFileException {
        File filePath = null;
        try {
            filePath = new File(getUserDirectory(), String.valueOf(fileDto.getId()));
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
        } catch (Exception ex) {
            throw new OFileException("Unable to create new file");
        }
        return filePath;
    }

    private File getUserDirectory(){
        File file = new File(DOWNLOAD_DIR, AuthorizationUtils.getUserEmail());
        file.mkdir();
        return file;
    }

    private void updateDownloadProgress(FileDto fileDto, long bytesRead) {
        progressCacheService.setFileProgress(fileDto.getId(),
            calculatePercentage(bytesRead, fileDto.getFileSize()));
    }

}
