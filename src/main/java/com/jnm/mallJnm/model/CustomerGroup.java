package com.jnm.mallJnm.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerGroup {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 组ID (主键)

    @NotBlank(message = "客户组名称不能为空")
    private String name; // 组名称

    private String description; // 组描述 (可选)

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间
}