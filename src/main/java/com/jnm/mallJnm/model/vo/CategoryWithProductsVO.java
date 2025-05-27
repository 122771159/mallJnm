package com.jnm.mallJnm.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryWithProductsVO {

    private String categoryId; // 分类ID (ProductCategory.java 中是 String)
    private String categoryName; // 分类名称
    private Integer sortOrder; // 分类排序权重
    private String description; // 分类描述
    private Boolean isShow; // 分类是否显示
    private List<ProductDisplayVO> products; // 该分类下的商品列表（包含最终价格）

    // 如果需要，可以添加分页信息，例如：
    // private long productTotal;
    // private long productPages;
    // private long productCurrentPage;
    // private long productPageSize;
}