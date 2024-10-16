package com.cs7rishi.oFile.service.impl;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class DownloadProgressCacheService {
    private final ConcurrentHashMap<Long, Integer> fileProgressCache = new ConcurrentHashMap();

    int getFileProgress(Long fileId) {
        return fileProgressCache.getOrDefault(fileId,-1);
    }

    void setFileProgress(Long fileId, int progress) {
        fileProgressCache.put(fileId, progress);
    }

    void deleteFileProgress(Long fileId){
        fileProgressCache.remove(fileId);
    }

    void initiateProgress(Long fileId){
        setFileProgress(fileId,0);
    }

}
