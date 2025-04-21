package com.example.btl_ltw.service.Impl;

import com.cloudinary.Cloudinary;
import com.example.btl_ltw.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    @Async
    public CompletableFuture<String> uploadFile(MultipartFile file) {
        try {
            Map data = this.cloudinary.uploader().upload(file.getBytes(), Map.of());
            String url = (String) data.getOrDefault("url", null);
            return CompletableFuture.completedFuture(url);
        } catch (IOException io) {
            throw new RuntimeException("Image upload fail", io);
        }
    }


}
