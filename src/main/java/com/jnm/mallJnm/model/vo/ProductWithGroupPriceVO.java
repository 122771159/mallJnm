package com.jnm.mallJnm.model.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jnm.mallJnm.model.CustomerProductPrice;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("jnm_customer_product_price")
public class ProductWithCustomerPriceVO extends CustomerProductPrice {
    // 商品基础信息
    private Long id;
    private String productName;
    private String productCode;
    private String description;
    private String mainImage;  // 主图URL
    private BigDecimal standardPrice;  // 标准价格
    private Integer stock;

    public void setMainImage(String images) {
        if (images == null || images.isEmpty()) {
            this.mainImage =  null;
        }else{
            String[] urls = images.split(",");
            this.mainImage = urls[0].trim();
        }
    }

}