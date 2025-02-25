package com.example.btl_ltw.service;

import org.springframework.web.multipart.MultipartFile;


public interface FileService {

    String uploadFile(MultipartFile file);
}
