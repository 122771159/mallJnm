package com.jnm.mallJnm.controller;

import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.exception.ValidatedException;
import com.jnm.mallJnm.model.UploadFile;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.service.UploadFileService;
import com.jnm.mallJnm.util.MinioUtil;
import com.jnm.mallJnm.util.UUIDFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;


@RestController
public class FileController {
    private static final String SUFFIX = ".jpg,.jpeg,.png";

    @Autowired
    UploadFileService uploadFileService;

    @PostMapping("/image")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new ValidatedException(ErrorEnum.EMPTY_FILE_ERROR);
        }
        if (file.getSize() > 1024 * 1024) {
            throw new ValidatedException(213, "文件大小不能大于1M");
        }

        // 检查文件类型
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new ValidatedException(ErrorEnum.EMPTY_FILE_ERROR);
        }
        String afterFix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        if (!SUFFIX.contains(afterFix)) {
            throw new ValidatedException(214, "请选择jpg,jpeg,png格式的图片");
        }

        // 生成新的文件名
        String id = UUIDFactory.random();
        String newFileName = id + afterFix;

        // 保存文件到服务器
        try {
            MinioUtil.upload(newFileName, file.getInputStream(), file.getSize());
        } catch (Exception e) {
            throw new ServerException(ErrorEnum.UPLOAD_FILE_ERROR);
        }

        // 保存文件信息到数据库
        saveUpload(id, fileName);

        // 返回图片的相对链接地址
        return newFileName;
    }

    @GetMapping("/image/{name}")
    public void getImage(@PathVariable String name, HttpServletResponse response) {
        response.setContentType(determineContentType(name));
        // 将图片流写入响应输出流
        try (InputStream in = MinioUtil.getObject(name)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/image/{name}")
    public void deleteImage(@PathVariable String name) {
        try {
            MinioUtil.removeObject(name);
        } catch (Exception e) {
            throw new ServerException(ErrorEnum.DELETE_FILE_ERROR);
        }
    }

    private String determineContentType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG_VALUE;
        } else if (fileName.endsWith(".png")) {
            return MediaType.IMAGE_PNG_VALUE;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE; // 默认二进制流
        }
    }

    private void saveUpload(String id, String name) {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setId(id);
        uploadFile.setPath("");
        uploadFile.setName(name);
        uploadFile.setUsed(false);
        uploadFile.setCreateTime(LocalDateTime.now());
        uploadFileService.save(uploadFile);
    }
}
