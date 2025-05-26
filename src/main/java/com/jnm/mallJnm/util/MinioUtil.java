package com.jnm.mallJnm.util;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * 类功能描述
 * Author: 魏思冉
 * Date: 2025/3/19
 */
@Slf4j
public class MinioUtil {
    private final static String ENDPOINT = "http://jnm.jnmtk.com:19000";
    private final static String ACCESS_KEY = "1HeoiFKqaik3Tw2iD6r7";
    private final static String SECRET_KEY = "1avVarEbOuYO4YMhh0m81Ug776lEM5DIBQ7lxtXz";
    private final static String BUCKET_NAME = "jnm";
    private static MinioClient minioClient;

    public static void upload(String objectName, InputStream inputStream, long contentLength) throws Exception {
        try {
            MinioClient minioClient = getMinioClient();
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)//存储桶
                    .object(objectName)//文件名
                    .stream(inputStream, contentLength, -1)//文件流
                    .contentType("application/octet-stream")//文件类型(都采用二进制流)
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            log.error("上传失败,原因：{}", e.getMessage());
            throw e;
        }
    }

    public static InputStream getObject(String objectName) throws Exception {
        try {
            MinioClient minioClient = getMinioClient();
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .build();
            return minioClient.getObject(getObjectArgs);
        } catch (Exception e) {
            log.error("获取文件失败,原因：{}", e.getMessage());
            throw e;
        }
    }

    public static void removeObject(String objectName) throws Exception {
        try {
            MinioClient minioClient = getMinioClient();
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("删除文件失败,原因：{}", e.getMessage());
            throw e;
        }
    }

    private static MinioClient getMinioClient() {
        if (minioClient == null) {
            minioClient = MinioClient.builder()
                    .endpoint(ENDPOINT)
                    .credentials(ACCESS_KEY, SECRET_KEY)
                    .build();
        }
        return minioClient;
    }
}
