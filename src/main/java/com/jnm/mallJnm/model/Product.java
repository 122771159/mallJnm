package com.jnm.mallJnm.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer categoryId;
    private String name;
    private String code;
    private String description;
    private String images; // 图片URL集合，多个用逗号分隔，第一个为主图
    private BigDecimal price;
    private BigDecimal costPrice;
    private Integer stock;
    private Integer sales;
    private Boolean isPublished;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // ----------------------------
    // 非数据库字段的便捷方法
    // ----------------------------
    
    /**
     * 获取主图URL
     */
    public String getMainImage() {
        if (images == null || images.isEmpty()) {
            return null;
        }
        String[] urls = images.split(",");
        return urls[0].trim();
    }
    
    /**
     * 获取所有图片URL数组
     */
    public String[] getAllImages() {
        if (images == null || images.isEmpty()) {
            return new String[0];
        }
        return images.split(",");
    }
    
    /**
     * 添加图片URL
     */
    public void addImage(String imageUrl) {
        if (images == null || images.isEmpty()) {
            images = imageUrl;
        } else {
            images += "," + imageUrl;
        }
    }
}