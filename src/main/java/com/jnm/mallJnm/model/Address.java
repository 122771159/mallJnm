package com.jnm.mallJnm.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Address {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @NotBlank(message = "地址不可为空")
    private String address;
    @NotBlank(message = "手机号不可为空")
    private String phone;
    @NotBlank(message = "姓名不可为空")
    private String name;

    private Integer isDefault;
    // 客户id
    private String cid;
    private LocalDateTime createTime;
}
