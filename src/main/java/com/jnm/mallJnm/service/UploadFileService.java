package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.UploadFile;

import java.util.List;

public interface UploadFileService extends IService<UploadFile> {
    void setUsed(List<String> files, Boolean used);
}
