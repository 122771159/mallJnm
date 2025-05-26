package com.jnm.mallJnm.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GroupProductPrice {

    @TableId(type = IdType.ASSIGN_UUID) // 或者使用 ASSIGN_UUID
    private String id; // 主键

    @TableField("group_id")
    private String groupId; // 客户组ID (外键，关联 CustomerGroup 表的 id)

    @TableField("product_id")
    private String productId; // 商品ID (外键，关联 Product 表的 id)

    @TableField("custom_price")
    private BigDecimal customPrice; // 该组客户对该商品的专属价格

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间



}