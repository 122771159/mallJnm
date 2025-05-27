package com.jnm.mallJnm.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Customer {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;  // 客户ID
    @NotBlank(message = "客户名称不能为空")
    private String name;  // 客户名称
    @NotBlank(message = "登录账号不能为空")
    private String account;  // 登录账号
    @JsonIgnore
    private String password;  // 登录密码
    private String openid;  // 微信openid（用于微信登录关联）
    private Integer status;  // 状态（0-禁用，1-启用）
    @TableField(condition = "group_id", updateStrategy = FieldStrategy.IGNORED) // 数据库中列名为 group_id
    private String groupId; // 所属客户组ID (外键，关联 CustomerGroup 表的 id)
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}