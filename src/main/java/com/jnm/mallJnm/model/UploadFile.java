package com.jnm.mallJnm.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UploadFile {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private String path;
    private boolean used;
    private LocalDateTime createTime;
}
