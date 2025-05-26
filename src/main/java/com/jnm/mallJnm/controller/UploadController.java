package com.jnm.mallJnm.controller;

import com.jnm.mallJnm.util.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping
    public String upload(MultipartFile file) throws Exception {
        //把文件的内容存储到本地磁盘上
        String originalFilename= file.getOriginalFilename();
        String filename= UUID.randomUUID() +originalFilename.substring(originalFilename.lastIndexOf("."));
        String url= aliOssUtil.uploadFile(filename, file.getInputStream());
        return url;
    }


}
