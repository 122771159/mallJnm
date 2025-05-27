package com.jnm.mallJnm.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDisplayVO {
    private String id; // 商品ID
    private String name; // 商品名称
    private String code; // 商品编码
    private String description; // 商品描述
    private String mainImage; // 商品主图
    private BigDecimal displayPrice; // 最终展示价格
    private Integer stock; // 库存
    private String categoryId; // 分类ID (注意：Product.java 中是Integer, ProductCategory.java中是String, 请统一或转换)
    private String categoryName; // 分类名称 (可选，如果需要一起展示)
    private Integer sortOrder; // 商品排序权重
    private Boolean isPublished; // 是否上架

}