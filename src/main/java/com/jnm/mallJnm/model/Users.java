package com.jnm.mallJnm.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;


@Data
public class Users {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @NotBlank(message = "用户名不可为空")
    private String username;
    private String password;
    @NotBlank(message = "名称不可为空")
    private String userType;
    private LocalDateTime createTime; // 创建时间
}
