package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.mapper.UploadFileMapper;
import com.jnm.mallJnm.model.UploadFile;
import com.jnm.mallJnm.service.UploadFileService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UploadFileServiceImpl extends ServiceImpl<UploadFileMapper, UploadFile> implements UploadFileService {
    @Override
    public void setUsed(List<String> files,Boolean used) {
        List<String> ids = files.stream()
                .map(file -> {
                    int lastDotIndex = file.lastIndexOf('.');
                    return lastDotIndex != -1 ? file.substring(0, lastDotIndex) : file;
                })
                .collect(Collectors.toList());
        if(ids.isEmpty()) return;
        LambdaUpdateWrapper<UploadFile> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(UploadFile::getId, ids);
        updateWrapper.set(UploadFile::isUsed, used);
        this.update(updateWrapper);
    }

}
