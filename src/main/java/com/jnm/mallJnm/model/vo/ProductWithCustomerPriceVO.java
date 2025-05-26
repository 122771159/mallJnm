package com.jnm.mallJnm.model.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("jnm_customer_product_price")
public class ProductWithCustomerPriceVO {
    // 商品基础信息
    private Long id;
    private Long productId;
    private String productName;
    private String productCode;
    private String description;
    private String mainImage;  // 主图URL
    private BigDecimal standardPrice;  // 标准价格
    private Integer stock;
    // 客户专属信息
    private BigDecimal customPrice;  // 客户专属价格
    private Boolean hasCustomPrice; // 是否有专属价格

    public void setMainImage(String images) {
        if (images == null || images.isEmpty()) {
            this.mainImage =  null;
        }else{
            String[] urls = images.split(",");
            this.mainImage = urls[0].trim();
        }

    }
}