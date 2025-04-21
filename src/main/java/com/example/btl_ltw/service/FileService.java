package com.example.btl_ltw.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;


public interface FileService {

    CompletableFuture<String> uploadFile(MultipartFile file);
}
